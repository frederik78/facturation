package info.minatchy.facturation;

import info.minatchy.facturation.model.Client;
import info.minatchy.facturation.model.Issuer;
import info.minatchy.facturation.model.Invoice;
import info.minatchy.facturation.repository.ClientRepository;
import info.minatchy.facturation.repository.InvoiceRepository;
import info.minatchy.facturation.repository.IssuerRepository;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;

public final class TestRepoFactory {

    private TestRepoFactory() {}

    public static IssuerRepository issuerRepo(long count, List<Issuer> list) {
        return (IssuerRepository) Proxy.newProxyInstance(IssuerRepository.class.getClassLoader(),
                new Class[]{IssuerRepository.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("count".equals(name)) return count;
                    if ("findAll".equals(name)) return list == null ? List.of() : list;
                    if ("save".equals(name)) return args[0];
                    return defaultResult(method);
                });
    }

    public static IssuerRepository issuerRepo(long count) {
        return issuerRepo(count, List.of());
    }

    public static InvoiceRepository invoiceRepoWithList(List<Invoice> invoices) {
        return (InvoiceRepository) Proxy.newProxyInstance(InvoiceRepository.class.getClassLoader(),
                new Class[]{InvoiceRepository.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("findAllByOrderByInvoiceDateDesc".equals(name)) return invoices;
                    if ("findAll".equals(name)) return invoices;
                    if ("save".equals(name)) {
                        Invoice e = (Invoice) args[0];
                        if (e.getId() == null) e.setId(1L);
                        return e;
                    }
                    if ("findById".equals(name)) {
                        Long id = (Long) args[0];
                        return invoices.stream().filter(iv -> iv.getId() != null && iv.getId().equals(id)).findFirst();
                    }
                    return defaultResult(method);
                });
    }

    public static InvoiceRepository invoiceRepoWithFindById(Invoice inv) {
        return (InvoiceRepository) Proxy.newProxyInstance(InvoiceRepository.class.getClassLoader(),
                new Class[]{InvoiceRepository.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("findById".equals(name)) return Optional.of(inv);
                    if ("save".equals(name)) return args[0];
                    if ("findAllByOrderByInvoiceDateDesc".equals(name)) return List.of(inv);
                    return defaultResult(method);
                });
    }

    public static InvoiceRepository invoiceRepoEmpty() {
        return (InvoiceRepository) Proxy.newProxyInstance(InvoiceRepository.class.getClassLoader(),
                new Class[]{InvoiceRepository.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("findAllByOrderByInvoiceDateDesc".equals(name)) return List.of();
                    if ("save".equals(name)) return args[0];
                    return defaultResult(method);
                });
    }

    public static ClientRepository clientRepoDefault() {
        return (ClientRepository) Proxy.newProxyInstance(ClientRepository.class.getClassLoader(),
                new Class[]{ClientRepository.class},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if ("findAllByOrderByNameAsc".equals(name)) return List.of(new Client());
                    if ("findById".equals(name)) {
                        Long id = (Long) args[0];
                        Client c = new Client(); c.setId(id); return Optional.of(c);
                    }
                    if ("save".equals(name)) {
                        Client e = (Client) args[0]; if (e.getId() == null) e.setId(5L); return e;
                    }
                    if ("deleteById".equals(name)) return null;
                    return defaultResult(method);
                });
    }

    private static Object defaultResult(java.lang.reflect.Method method) {
        Class<?> rt = method.getReturnType();
        if (rt.equals(void.class)) return null;
        if (rt.equals(boolean.class)) return false;
        if (rt.equals(long.class)) return 0L;
        if (rt.equals(Optional.class)) return Optional.empty();
        if (rt.isAssignableFrom(List.class)) return List.of();
        return null;
    }
}
