def coin_breakdown(amount):
    # List of available denominations in DESC order
    denominations = [1000, 500, 200, 100, 50, 20, 10, 5, 1]
    
    # Create an empty dictionary to store the count for each denominations
    breakdown = {}
    
    #Iterate over each denominations
    for i in denominations:
        # Calculate how many times this denominations fits into the amount.
        count = amount // i # Integer division to get the count
        breakdown[i] = count # Store the count in the dictionary.
        amount = amount % i # Update the amount to the remainder.
    return breakdown

def main():
    # ask the user to enter an amount
    num = int(input("Input an amount: "))
    
    #  Get the breakdown of coins/bills
    breakdown = coin_breakdown(num)
    
    # print the result in the desired format
    print("\nOutput: ")
    for i in [1000, 500, 200, 100, 50, 20, 10, 5, 1]:
        print(f"{i} - {breakdown[i]}")
        
if __name__ =="__main__":
    main() 