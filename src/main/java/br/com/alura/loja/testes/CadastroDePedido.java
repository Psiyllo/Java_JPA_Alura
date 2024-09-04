package br.com.alura.loja.testes;

import br.com.alura.loja.dao.CategoriaDao;
import br.com.alura.loja.dao.ClienteDao;
import br.com.alura.loja.dao.PedidoDao;
import br.com.alura.loja.dao.ProdutoDao;
import br.com.alura.loja.modelo.*;
import br.com.alura.loja.util.JPAUtil;
import br.com.alura.loja.vo.RelatorioDeVendasVo;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.util.List;

public class CadastroDePedido {
    public static void main(String[] args) {
        PopularBancoDeDados();

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            ProdutoDao produtoDao = new ProdutoDao(em);
            ClienteDao clienteDao = new ClienteDao(em);

            // Buscar produtos e cliente
            Produto celular = produtoDao.buscarPorId(1L);  // Xiaomi Redmi
            Produto notebook = produtoDao.buscarPorId(2L); // Notebook Intel
            Produto aparelho = produtoDao.buscarPorId(3L); // Monitor AOC
            Cliente cliente1 = clienteDao.buscarPorId(1L); // Cliente Paulo
            Cliente cliente2 = clienteDao.buscarPorId(2L); // Novo cliente

            // Primeiro Pedido
            Pedido pedido1 = new Pedido(cliente1);
            pedido1.adicionarItem(new ItemPedido(celular, pedido1, 10));
            pedido1.adicionarItem(new ItemPedido(notebook, pedido1, 3));
            PedidoDao pedidoDao = new PedidoDao(em);
            pedidoDao.cadastrar(pedido1);

            // Segundo Pedido
            Pedido pedido2 = new Pedido(cliente2);
            pedido2.adicionarItem(new ItemPedido(notebook, pedido2, 5));
            pedido2.adicionarItem(new ItemPedido(aparelho, pedido2, 60));
            pedidoDao.cadastrar(pedido2);

            transaction.commit();

            // Relatórios e Resultados
            BigDecimal totalVendido = pedidoDao.valorTotalVendido();
            System.out.println("Valor total vendido: " + totalVendido);

            BigDecimal minimoVendido = pedidoDao.valorMinimoVendido();
            System.out.println("Valor mínimo vendido: " + minimoVendido);

            BigDecimal maximoVendido = pedidoDao.valorMaximoVendido();
            System.out.println("Valor máximo vendido: " + maximoVendido);

            List<RelatorioDeVendasVo> relatorio = pedidoDao.relatorioDeVendas();
            relatorio.forEach(System.out::println);

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private static void PopularBancoDeDados() {
        Categoria celulares = new Categoria("CELULARES");
        Categoria notebooks = new Categoria("NOTEBOOKS");
        Categoria aparelhos = new Categoria("APARELHOS");

        Produto celular = new Produto("Xiaomi Redmi", "Muito legal", new BigDecimal("800"), celulares);
        Produto notebook = new Produto("Notebook Intel", "Só o básico", new BigDecimal("1750"), notebooks);
        Produto aparelho = new Produto("Monitor AOC", "TOP dms", new BigDecimal("720"), aparelhos);

        Cliente cliente1 = new Cliente("Paulo", "123456");
        Cliente cliente2 = new Cliente("Ana", "654321");

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Cadastrar categorias
            CategoriaDao categoriaDao = new CategoriaDao(em);
            categoriaDao.cadastrar(celulares);
            categoriaDao.cadastrar(notebooks);
            categoriaDao.cadastrar(aparelhos);

            // Cadastrar produtos
            ProdutoDao produtoDao = new ProdutoDao(em);
            produtoDao.cadastrar(celular);
            produtoDao.cadastrar(notebook);
            produtoDao.cadastrar(aparelho);

            // Cadastrar clientes
            ClienteDao clienteDao = new ClienteDao(em);
            clienteDao.cadastrar(cliente1);
            clienteDao.cadastrar(cliente2);

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
