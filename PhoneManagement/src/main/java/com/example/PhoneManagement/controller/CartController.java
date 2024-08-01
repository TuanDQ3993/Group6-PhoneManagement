package com.example.PhoneManagement.controller;

import com.example.PhoneManagement.dto.request.UserDTO;
import com.example.PhoneManagement.entity.ProductInfo;
import com.example.PhoneManagement.entity.Users;
import com.example.PhoneManagement.service.imp.CartService;
import com.example.PhoneManagement.service.imp.UserService;
import com.example.PhoneManagement.util.Cart;
import com.example.PhoneManagement.util.Item;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @PostMapping("/addtocart")
    public String addtocart(@RequestParam("productColorId") int productColorId, Model model, HttpSession session, Principal principal) {
        if (principal == null) {
            model.addAttribute("error", "You need login before adding to cart.");
            return "login";
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        }
        ProductInfo productInfo = cartService.getProductInfoById(productColorId);
        double price = productInfo.getPrice().doubleValue();
        Item item = new Item(productInfo, price, 1);
        cart.addItem(item);


        model.addAttribute("cart", cart);
        model.addAttribute("size", cart.getItems().size());
        model.addAttribute("total", cart.getTotalPrice());

        session.setAttribute("cart", cart);

        return "viewcart";
    }

    @PostMapping("/delete")
    public String deleteItem(@RequestParam("productColorId") int productColorId, Model model, HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        } else {
            cart.removeItem(productColorId);
        }
        session.setAttribute("cart", cart);
        return "redirect:/home/homepage";
    }

    @GetMapping("/remove")
    public String removeItem(@RequestParam("productColorId") int productColorId,
                             Model model,
                             HttpSession session,
                             Principal principal) {
        if (principal != null) {
            String userName = principal.getName();
            Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
            userDTO.ifPresent(user -> model.addAttribute("user", user));
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            model.addAttribute("cart", cart);
            model.addAttribute("size", cart.getItems().size());
            model.addAttribute("total", cart.getTotalPrice());
        } else {
            model.addAttribute("size", 0);
            model.addAttribute("total", 0.0);
        }
        cart.removeItem(productColorId);

        return "viewcart";
    }

    @GetMapping("checkout")
    public String checkout(Model model, HttpSession session, Principal principal) {

        if (principal != null) {
            String userName = principal.getName();
            Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
            userDTO.ifPresent(user -> model.addAttribute("user", user));
        } else {
            model.addAttribute("error", "You need login before want checkout.");
            return "login";
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            model.addAttribute("cart", cart);
            model.addAttribute("size", cart.getItems().size());
            model.addAttribute("total", cart.getTotalPrice());
        }
        else {
            model.addAttribute("size", 0);
            model.addAttribute("total", 0.0);
        }


        return "checkout";

    }

    @GetMapping("viewcart")
    public String viewCart(Model model, HttpSession session, Principal principal) {
        if (principal != null) {
            String userName = principal.getName();
            Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
            userDTO.ifPresent(user -> model.addAttribute("user", user));
        } else {
            model.addAttribute("error", "You need login before want view cart.");
            return "login";
        }


        Cart cart = (Cart) session.getAttribute("cart");
        if (cart != null) {
            model.addAttribute("cart", cart);
            model.addAttribute("size", cart.getItems().size());
            model.addAttribute("total", cart.getTotalPrice());
        } else {
            model.addAttribute("size", 0);
            model.addAttribute("total", 0.0);
        }

        return "viewcart";
    }

    @GetMapping("process")
    public String process(@RequestParam("num") int num,
                          @RequestParam("id") int id,
                          Model model,
                          HttpSession session,
                          Principal principal) {
        if (principal != null) {
            String userName = principal.getName();
            Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
            userDTO.ifPresent(user -> model.addAttribute("user", user));
        }
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        if (num == -1 && cart.getItemById(id).getQuantity() <= 1) {
            cart.removeItem(id);
        } else {
            ProductInfo productInfo = cartService.getProductInfoById(id);
            double price = productInfo.getPrice().doubleValue();
            cart.addItem(new Item(productInfo, price, num));
        }

        model.addAttribute("cart", cart);
        model.addAttribute("size", cart.getItems().size());
        model.addAttribute("total", cart.getTotalPrice());


        return "viewcart";
    }

    @PostMapping("placeorder")
    public String placeorder(@RequestParam("fullname") String fullname,
                             @RequestParam("address") String address,
                             @RequestParam("tel") String tel,
                             @RequestParam("note") String note,
                             @RequestParam("payment") String payment,
                             HttpSession session,Principal principal) {

        Cart cart = (Cart) session.getAttribute("cart");
        if(cart == null){
            return "redirect:/home/homepage";
        }
        String userName = principal.getName();
        Users users=userService.getUserByName(userName);
        cartService.addOrder(users,cart,fullname,address,tel,note,payment);
        return "redirect:/cart/ordersuccess";
    }

    @GetMapping("ordersuccess")
    public String orderSuccess(HttpSession session,Principal principal,Model model){

        if (principal != null) {
            String userName = principal.getName();
            Optional<UserDTO> userDTO = userService.getUserByUserName(userName);
            userDTO.ifPresent(user -> model.addAttribute("user", user));
        }

        Cart cart = (Cart) session.getAttribute("cart");
        List<Item> listi= cart.getItems();

        model.addAttribute("listi",listi);
        model.addAttribute("totalsuccess",cart.getTotalPrice());
        model.addAttribute("size",0);
        model.addAttribute("total",0.0);

        session.removeAttribute("cart");
        return "ordersuccess";
    }
}
