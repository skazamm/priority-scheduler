# init_db.py
import sqlite3

def init_db():
    conn = sqlite3.connect('employee.db')
    cursor = conn.cursor()

    cursor.execute('''
        CREATE TABLE IF NOT EXISTS employee_tbl (
            empNo TEXT PRIMARY KEY,
            empName TEXT NOT NULL,
            empStatus TEXT NOT NULL,
            empPosition TEXT NOT NULL,
            empSalary REAL NOT NULL
        )
    ''')

    conn.commit()
    conn.close()
    print("Database initialized.")

if __name__ == '__main__':
    init_db()
