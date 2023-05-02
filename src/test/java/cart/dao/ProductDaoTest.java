package cart.dao;

import cart.domain.product.Product;
import cart.persistance.dao.ProductDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class ProductDaoTest {

    private final ProductDao productDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductDaoTest(JdbcTemplate jdbcTemplate) {
        this.productDao = new ProductDao(jdbcTemplate);
    }

    @DisplayName("상품을 저장한다.")
    @Test
    void shouldSaveProductWhenRequest() {
        final Product productToSave = Product.createWithoutId("changer", 10, "domain.com");
        final long productId = productDao.add(productToSave);
        final String sql = "SELECT id, name, price, image_url FROM product WHERE id = ?";

        final Product productFromDb = jdbcTemplate.queryForObject(sql,
                (resultSet, rowNumber) -> Product.create(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getLong("price"),
                        resultSet.getString("image_url"))
                , productId);

        assertAll(
                () -> assertThat(productFromDb.getName()).isEqualTo("changer"),
                () -> assertThat(productFromDb.getPrice()).isEqualTo(10),
                () -> assertThat(productFromDb.getImageUrl()).isEqualTo("domain.com")
        );
    }

    @DisplayName("상품 전체를 조회한다.")
    @Test
    void shouldReturnAllProductsWhenRequest() {
        jdbcTemplate.update("INSERT INTO product (name, price, image_url) VALUES (?, ?, ?)", "사과", 100, "domain.com");
        jdbcTemplate.update("INSERT INTO product (name, price, image_url) VALUES (?, ?, ?)", "당근", 100, "domain.com");

        final List<Product> products = productDao.findAll();

        assertAll(
                () -> assertThat(products).hasSize(2),
                () -> assertThat(products.get(0).getName()).isEqualTo("사과"),
                () -> assertThat(products.get(0).getPrice()).isEqualTo(100),
                () -> assertThat(products.get(0).getImageUrl()).isEqualTo("domain.com"),
                () -> assertThat(products.get(1).getName()).isEqualTo("당근")
        );
    }

    @DisplayName("상품을 수정한다.")
    @Test
    void shouldUpdateWhenRequest() {
        //given
        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO product (name, price, image_url) VALUES (?, ?, ?)", new String[]{"id"});
            preparedStatement.setString(1, "사과");
            preparedStatement.setLong(2, 100);
            preparedStatement.setString(3, "domain.com");
            return preparedStatement;
        }, keyHolder);

        long productId = keyHolder.getKey().longValue();
        Product productToUpdate = Product.create(
                productId,
                "당근",
                1000,
                "domain.kr"
        );

        //when
        productDao.update(productToUpdate);

        Product productAfterUpdate = jdbcTemplate.queryForObject(
                "SELECT id, name, price, image_url FROM product WHERE id = ?",
                (resultSet, rowNumber) -> Product.create(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getLong("price"),
                        resultSet.getString("image_url")
                ), productId);

        //then
        assertAll(
                () -> assertThat(productAfterUpdate.getId()).isEqualTo(productToUpdate.getId()),
                () -> assertThat(productAfterUpdate.getName()).isEqualTo(productToUpdate.getName()),
                () -> assertThat(productAfterUpdate.getPrice()).isEqualTo(productToUpdate.getPrice()),
                () -> assertThat(productAfterUpdate.getImageUrl()).isEqualTo(productToUpdate.getImageUrl())
        );
    }

    @DisplayName("상품을 삭제한다.")
    @Test
    void shouldDeleteWhenRequest() {

        //given
        final KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update("INSERT INTO product (name, price, image_url) VALUES (?, ?, ?)", "당근", 1000, "domain.com");
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO product (name, price, image_url) VALUES (?, ?, ?)", new String[]{"id"});
            preparedStatement.setString(1, "사과");
            preparedStatement.setLong(2, 100);
            preparedStatement.setString(3, "domain.com");
            return preparedStatement;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();

        //when
        productDao.deleteById(id);

        List<Product> products = jdbcTemplate.query(
                "SELECT id, name, price, image_url FROM product",
                (resultSet, rowNumber) -> Product.create(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getLong("price"),
                        resultSet.getString("image_url")
                ));

        //then
        assertThat(products).hasSize(1);
    }
}
