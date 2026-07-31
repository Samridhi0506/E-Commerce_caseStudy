/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { useAuth } from "./AuthContext";
import {
  getCart,
  addToCart as addToCartApi,
  updateCartQuantity,
  removeFromCart as removeFromCartApi,
} from "../services/cartService";

const CartContext = createContext();

export function CartProvider({ children }) {
  const { isAuthenticated, userId } = useAuth();

  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(false);

  const loadCart = async () => {
    if (!isAuthenticated || !userId) {
      setCartItems([]);
      return;
    }

    try {
      setLoading(true);
      const response = await getCart(userId);
      setCartItems(response.data || []);
    } catch (error) {
      console.error("Failed to load cart", error);
      setCartItems([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCart();
  }, [isAuthenticated, userId]);

  const refreshCart = () => {
    loadCart();
  };

  const cartCount = useMemo(() => {
    return cartItems.reduce(
      (total, item) => total + item.quantity,
      0
    );
  }, [cartItems]);

  const getCartItem = (productId) => {
    return (
      cartItems.find(
        (item) => item.productId === productId
      ) || null
    );
  };

  const getCartQuantity = (productId) => {
    const item = getCartItem(productId);
    return item ? item.quantity : 0;
  };

  const addToCart = async (productId) => {
    await addToCartApi(userId, productId, 1);
    await loadCart();
  };

  const increaseQuantity = async (productId) => {
    const item = getCartItem(productId);

    if (!item) {
      return addToCart(productId);
    }

    await updateCartQuantity(
      item.cartId,
      item.quantity + 1
    );

    await loadCart();
  };

  const decreaseQuantity = async (productId) => {
    const item = getCartItem(productId);

    if (!item) return;

    if (item.quantity === 1) {
      await removeFromCartApi(item.cartId);
    } else {
      await updateCartQuantity(
        item.cartId,
        item.quantity - 1
      );
    }

    await loadCart();
  };

  const removeItem = async (productId) => {
  const item = getCartItem(productId);

  if (!item) return;

  await removeFromCartApi(item.cartId);

  await loadCart();
};

  return (
    <CartContext.Provider
      value={{
        loading,
        cartItems,
        cartCount,
        refreshCart,
        addToCart,
        increaseQuantity,
        decreaseQuantity,
        removeItem,
        getCartItem,
        getCartQuantity,
      }}
    >
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  return useContext(CartContext);
}