package cart.persistance.dao;

import cart.domain.cart.Cart;
import cart.domain.cart.CartItem;
import cart.domain.product.Product;
import cart.persistance.dao.exception.ProductNotFoundException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class CartDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CartDao(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Cart findByUserId(final long memberId) {
        final String sql = "SELECT cart.id, product.id, product.name, product.price, product.image_url " +
                "FROM cart " +
                "JOIN product " +
                "ON cart.product_id = product.id " +
                "WHERE cart.member_id = :member_id";
        final var sqlParameterSource = new MapSqlParameterSource("member_id", memberId);
        final List<CartItem> cartItems = jdbcTemplate.query(sql, sqlParameterSource, (rs, rowNum) ->
                new CartItem(rs.getLong("cart.id"),
                        Product.create(
                                rs.getLong("product.id"),
                                rs.getString("product.name"),
                                rs.getLong("product.price"),
                                rs.getString("product.image_url")
                        )));

        return new Cart(cartItems);
    }

    public long addProduct(final long memberId, final long productId) {
        final String sql = "INSERT INTO cart (member_id, product_id) VALUES (:member_id, :product_id)";
        final var sqlParameterSource =
                new MapSqlParameterSource(Map.of("member_id", memberId, "product_id", productId));
        final var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, sqlParameterSource, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void removeById(final long id) {
        final String sql = "DELETE FROM cart " +
                "WHERE id = :id";
        final var sqlParameterSource =
                new MapSqlParameterSource("id", id);
        final int affected = jdbcTemplate.update(sql, sqlParameterSource);

        if (affected == 0) {
            throw new ProductNotFoundException();
        }
    }

}
