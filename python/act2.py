import os

class Product:
    def __init__(self, product_number, product_name, initial_quantity, price):
        self.product_number = product_number
        self.product_name = product_name
        self.initial_quantity = initial_quantity
        self.available_stock = initial_quantity  # Current quantity in stock
        self.price = price  # Price of the product

    def __str__(self):
        return f"Product Number: {self.product_number}, Product Name: {self.product_name}, Quantity: {self.available_stock}, Price: {self.price}"

    def purchase(self, quantity):
        """Increase the quantity of the product."""
        self.available_stock += quantity

    def sell(self, quantity):
        """Decrease the quantity of the product and return the total cost of sold quantity."""
        if quantity > self.available_stock:
            raise ValueError("Not enough stock available!")
        self.available_stock -= quantity
        return quantity * self.price

# Method to handle integer input with exception handling
def input_integer(message):
    while True:
        try:
            value = input(message).strip()
            if not value.isdigit():
                raise ValueError("Input must be a positive integer.")
            value = int(value)
            if value <= 0:
                raise ValueError("Value must be positive.")
            return value
        except ValueError as e:
            print(f"Invalid input: {e}")

# Method to handle floating-point input with exception handling
def input_float(message):
    while True:
        try:
            value = input(message).strip()
            value = float(value)
            if value <= 0:
                raise ValueError("Price must be a positive value.")
            return value
        except ValueError as e:
            print(f"Invalid input: {e}")

# Method to add product to the file
def add_product():
    product_number = input_integer("Enter Product Number: ")
    product_name = input("Enter Product Name: ")
    initial_quantity = input_integer("Enter Quantity: ")
    price = input_float("Enter Product Price: ")
    product = Product(product_number, product_name, initial_quantity, price)

    # Save product to file
    with open('product.txt', 'a') as file:
        file.write(f"{product.product_number},{product.product_name},{product.initial_quantity},{product.price}\n")

# Method to load products from the file
def load_products():
    products = {}
    if os.path.exists('product.txt'):
        with open('product.txt', 'r') as file:
            for line in file:
                data = line.strip().split(',')
                product = Product(int(data[0]), data[1], int(data[2]), float(data[3]))
                products[product.product_number] = product
    return products

# Method to save products back to the file after updates
def save_products(products):
    with open('product.txt', 'w') as file:
        for product in products.values():
            file.write(f"{product.product_number},{product.product_name},{product.available_stock},{product.price}\n")

# Method to search for a product by its number
def search_product(products):
    product_number = input_integer("Enter Product Number to search: ")
    if product_number in products:
        return products[product_number]
    else:
        print("Record Not Found!")
        return None

# Method to delete a product from the file
def delete_product(products):
    product = search_product(products)
    if product:
        confirm = input(f"Are you sure you want to delete {product.product_name}? (y/n): ").strip().lower()
        if confirm == 'y':
            del products[product.product_number]
            print(f"Product {product.product_name} deleted.")
        else:
            print("Deletion cancelled.")
        # Save the updated products back to the file
        save_products(products)

# Method to handle transactions for purchasing or selling
def transaction(products):
    product = search_product(products)
    if product:
        action = input("Enter transaction (purchase/sell): ").strip().lower()
        if action == 'purchase':
            quantity = input_integer("Enter quantity to purchase: ")
            product.purchase(quantity)
            print(f"New quantity of {product.product_name}: {product.available_stock}")
        elif action == 'sell':
            quantity = input_integer("Enter quantity to sell: ")
            try:
                total_cost = product.sell(quantity)
                print(f"Total cost of the sale: {total_cost}")
            except ValueError as e:
                print(f"Error: {e}")
        else:
            print("Invalid action! Please enter 'purchase' or 'sell'.")

        # Save the updated product information to the file
        save_products(products)

# Method to display product records
def display_products(products):
    print("\nProduct Records:")
    if products:
        for product in products.values():
            print(product)
    else:
        print("No products available.")

# Main Program
def main():
    products = load_products()

    while True:
        print("\nMenu:")
        print("1. Add Product")
        print("2. View Products")
        print("3. Transaction (purchase/sell)")
        print("4. Delete Product")
        print("5. Exit")
        choice = input_integer("\nEnter your choice: ")

        if choice == 1:
            add_product()
        elif choice == 2:
            display_products(products)
        elif choice == 3:
            transaction(products)
        elif choice == 4:
            delete_product(products)
        elif choice == 5:
            print("Exiting the program.")
            break
        else:
            print("Invalid input! Please choose a valid option.")

if __name__ == "__main__":
    main()
