package controllers;

import java.util.Set;

import models.Product;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class Products extends Controller {
	
	public static Result list(){
		Set<Product> products=Product.findAll();
		return ok(views.html.list.render(products));
	}
	
	public static Result showBlank(){
		return ok(views.html.show.render(productForm));
	}
	
	public static Result show(Long ean){
		final Product product = Product.findByEan(ean);
		if(product==null){
			return notFound(String.format("Product %s does not exist.", ean));
		}
		
		Form<Product> filledForm=productForm.fill(product);
		return ok(views.html.show.render(filledForm));
	}
	
	public static Result delete(Long ean){
		final Product product = Product.findByEan(ean);
		if(product==null){
			return notFound(String.format("Product %s does not exist.", ean));
		}
		
		Product.remove(product);
		return redirect(routes.Products.list());
	}
	
	public static Result save(){
		Form<Product> boundForm=productForm.bindFromRequest();
		if(boundForm.hasErrors()){
			flash("error", "Please correct the form below.");
			return badRequest(views.html.show.render(boundForm));
		}
		
		Product product=boundForm.get();
		if(product.ean==null){
			flash("error", "Please at least enter something.");
			return badRequest(views.html.show.render(boundForm));
		}
		
		Set<Product> products=Product.findAll();
		for(Product _product : products){
			if(_product.ean.equals(product.ean)){
				Product.remove(_product);
				Product.add(product);
				flash("success", String.format("Successfully updated product %s", product));
				return redirect(routes.Products.list());
			}
		}
		
		Product.add(product);
		flash("success", String.format("Successfully added product %s", product));
		return redirect(routes.Products.list());
	}
	
	private static final Form<Product> productForm = new Form<Product>(Product.class);
}
