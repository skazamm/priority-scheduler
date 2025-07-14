import tkinter as tk
from tkinter import ttk, messagebox
import sqlite3

# --- DB connector ---
def get_db_connection():
    return sqlite3.connect('employee.db')

# --- Salary map ---
POSITION_SALARY = {
    "Clerk": 20000.0,
    "Accountant": 25000.0,
    "Sales Manager": 40000.0,
    "Production Staff": 22000.0,
    "Project Manager": 50000.0
}

# --- Add record ---
def add_record():
    empNo = emp_no_var.get().strip()
    empName = emp_name_var.get().strip()
    empStatus = emp_status_var.get()
    empPosition = position_var.get()
    empSalary = salary_var.get()

    if not empNo or not empName or not empStatus or not empPosition:
        messagebox.showerror("Input Error", "All fields are required!")
        return

    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute('''
            INSERT INTO employee_tbl (empNo, empName, empStatus, empPosition, empSalary)
            VALUES (?, ?, ?, ?, ?)
        ''', (empNo, empName, empStatus, empPosition, float(empSalary)))
        conn.commit()
        conn.close()
        messagebox.showinfo("Success", "Record added successfully.")
        clear_form()
        view_records()
    except sqlite3.IntegrityError:
        messagebox.showerror("Error", "Employee Number already exists.")
    except Exception as e:
        messagebox.showerror("Error", str(e))

# --- View all records ---
def view_records():
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT * FROM employee_tbl")
        records = cursor.fetchall()
        conn.close()

        output_text.config(state='normal')
        output_text.delete(1.0, tk.END)

        if not records:
            output_text.insert(tk.END, "No records found.")
        else:
            output_text.insert(tk.END, " EMPLOYEE RECORDS\n\n")
            for rec in records:
                output_text.insert(tk.END, f" EmpNo: {rec[0]}\n")
                output_text.insert(tk.END, f" Name: {rec[1]}\n")
                output_text.insert(tk.END, f" Status: {rec[2]}\n")
                output_text.insert(tk.END, f" Position: {rec[3]}\n")
                output_text.insert(tk.END, f" Salary: ₱{rec[4]:,.2f}\n")
                output_text.insert(tk.END, "-" * 60 + "\n\n")

        output_text.config(state='disabled')
    except Exception as e:
        messagebox.showerror("Error", str(e))

# --- Search by EmpNo ---
def search_record():
    empNo = emp_no_var.get().strip()
    if not empNo:
        messagebox.showwarning("Input Needed", "Enter Employee Number to search.")
        return

    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT * FROM employee_tbl WHERE empNo=?", (empNo,))
        result = cursor.fetchone()
        conn.close()

        if result:
            emp_no_var.set(result[0])
            emp_name_var.set(result[1])
            emp_status_var.set(result[2])
            position_var.set(result[3])
            salary_var.set(str(result[4]))
            messagebox.showinfo("Found", f"Record found for EmpNo {empNo}")
        else:
            messagebox.showinfo("Not Found", f"No record found for EmpNo {empNo}")
    except Exception as e:
        messagebox.showerror("Error", str(e))

# --- Update existing record ---
def update_record():
    empNo = emp_no_var.get().strip()
    empName = emp_name_var.get().strip()
    empStatus = emp_status_var.get()
    empPosition = position_var.get()
    empSalary = salary_var.get()

    if not empNo or not empName or not empStatus or not empPosition:
        messagebox.showerror("Input Error", "All fields are required!")
        return

    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute('''
            UPDATE employee_tbl
            SET empName=?, empStatus=?, empPosition=?, empSalary=?
            WHERE empNo=?
        ''', (empName, empStatus, empPosition, float(empSalary), empNo))
        conn.commit()
        conn.close()
        messagebox.showinfo("Success", "Record updated successfully.")
        clear_form()
        view_records()
    except Exception as e:
        messagebox.showerror("Error", str(e))

# --- Delete record ---
def delete_record():
    empNo = emp_no_var.get().strip()
    if not empNo:
        messagebox.showwarning("Input Needed", "Enter Employee Number to delete.")
        return

    confirm = messagebox.askyesno("Confirm Delete", f"Delete record for EmpNo {empNo}?")
    if not confirm:
        return

    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("DELETE FROM employee_tbl WHERE empNo=?", (empNo,))
        conn.commit()
        conn.close()

        if cursor.rowcount == 0:
            messagebox.showinfo("Not Found", "Employee number does not exist.")
        else:
            messagebox.showinfo("Deleted", "Record deleted successfully.")
            clear_form()
            view_records()
    except Exception as e:
        messagebox.showerror("Error", str(e))

# --- Clear form ---
def clear_form():
    emp_no_var.set("")
    emp_name_var.set("")
    emp_status_var.set("Permanent")
    position_var.set("")
    salary_var.set("")

# --- GUI Setup ---
root = tk.Tk()
root.title("Employee Record System")
root.geometry("720x720")

emp_no_var = tk.StringVar()
emp_name_var = tk.StringVar()
emp_status_var = tk.StringVar(value="Permanent")
position_var = tk.StringVar()
salary_var = tk.StringVar()

# --- Layout ---

# Employee Info
tk.Label(root, text="Employee Number:").grid(row=0, column=0, sticky='e', padx=(20, 5), pady=5)
tk.Entry(root, textvariable=emp_no_var, width=30).grid(row=0, column=1, sticky='w', padx=5)

tk.Label(root, text="Employee Name:").grid(row=1, column=0, sticky='e', padx=(20, 5), pady=5)
tk.Entry(root, textvariable=emp_name_var, width=30).grid(row=1, column=1, sticky='w', padx=5)

# Employment Status
tk.Label(root, text="Employment Status:").grid(row=2, column=0, sticky='ne', padx=(20, 5), pady=5)
status_frame = tk.Frame(root)
status_frame.grid(row=2, column=1, sticky='w')
for status in ["Permanent", "Probationary", "Casual", "Contractual"]:
    tk.Radiobutton(status_frame, text=status, variable=emp_status_var, value=status).pack(anchor='w')

# Position + Salary
tk.Label(root, text="Position:").grid(row=6, column=0, sticky='e', padx=(20, 5), pady=5)
position_combo = ttk.Combobox(root, textvariable=position_var, values=list(POSITION_SALARY.keys()), state="readonly", width=27)
position_combo.grid(row=6, column=1, sticky='w', padx=5)
position_combo.bind("<<ComboboxSelected>>", lambda e: salary_var.set(str(POSITION_SALARY[position_var.get()])))

tk.Label(root, text="Salary:").grid(row=7, column=0, sticky='e', padx=(20, 5), pady=5)
tk.Entry(root, textvariable=salary_var, state='readonly', width=30).grid(row=7, column=1, sticky='w', padx=5)

# Buttons
top_button_frame = tk.Frame(root)
top_button_frame.grid(row=8, column=0, columnspan=2, pady=(15, 5))
tk.Button(top_button_frame, text="ADD", width=12, command=add_record).pack(side='left', padx=10)
tk.Button(top_button_frame, text="UPDATE", width=12, command=update_record).pack(side='left', padx=10)
tk.Button(top_button_frame, text="DELETE", width=12, command=delete_record).pack(side='left', padx=10)

bottom_button_frame = tk.Frame(root)
bottom_button_frame.grid(row=9, column=0, columnspan=2, pady=5)
tk.Button(bottom_button_frame, text="VIEW", width=12, command=view_records).pack(side='left', padx=15)
tk.Button(bottom_button_frame, text="EXIT", width=12, command=root.quit).pack(side='left', padx=15)

# Text Area Output (Enhanced)
output_text = tk.Text(root, height=15, width=85, font=("Courier New", 10), state='disabled')
output_text.grid(row=10, column=0, columnspan=2, padx=10, pady=10)

# SEARCH Button (Floating near top-right)
tk.Button(root, text="SEARCH", width=10, command=search_record).place(x=570, y=27)

# Start
view_records()
root.mainloop()
