import os


# Student Class Definition
class Student:
    def __init__(self, student_number, student_name):
        self.student_number = student_number
        self.student_name = student_name
        self.subjects = []

    def add_subject(self, subject_code, subject_description, num_of_units, midterm_grade, final_grade):
        subject = {
            'subject_code': subject_code,
            'subject_description': subject_description,
            'num_of_units': num_of_units,
            'midterm_grade': midterm_grade,
            'final_grade': final_grade,
            'average_grade': self.calculate_average(midterm_grade, final_grade),
            'remarks': self.calculate_remarks(midterm_grade, final_grade)
        }
        self.subjects.append(subject)

    def calculate_average(self, midterm_grade, final_grade):
        return (midterm_grade + final_grade) / 2

    def calculate_remarks(self, midterm_grade, final_grade):
        average_grade = self.calculate_average(midterm_grade, final_grade)
        if average_grade <= 3.12:
            return 'Passed'
        else:
            return 'Failed'

    def display(self):
        print(f"Student Number: {self.student_number}")
        print(f"Student Name: {self.student_name}")
        for subject in self.subjects:
            print(f"Subject Code: {subject['subject_code']}")
            print(f"Subject Description: {subject['subject_description']}")
            print(f"Units: {subject['num_of_units']}")
            print(f"Midterm Grade: {subject['midterm_grade']}")
            print(f"Final Grade: {subject['final_grade']}")
            print(f"Average Grade: {subject['average_grade']}")
            print(f"Remarks: {subject['remarks']}")
            print('-' * 30)


# Method to handle integer input with exception handling
def input_integer(message):
    while True:
        try:
            value = int(input(message))
            if value <= 0:
                raise ValueError("Value must be positive.")
            return value
        except ValueError as e:
            print(f"Invalid input: {e}")


# Method to handle floating-point input with exception handling
def input_float(message):
    while True:
        try:
            value = float(input(message))
            if value < 0 or value > 5:  # Assuming grades must be between 0 and 5
                raise ValueError("Grades must be between 0 and 5.")
            return value
        except ValueError as e:
            print(f"Invalid input: {e}")


# Method to add a student record
def add_student():
    student_number = input_integer("Enter Student Number: ")
    student_name = input("Enter Student Name: ")

    student = Student(student_number, student_name)

    # Add subjects
    for i in range(7):  # A student can have up to 7 subjects
        print(f"\nEnter details for Subject {i + 1}")
        subject_code = input("Enter Subject Code: ")
        subject_description = input("Enter Subject Description: ")
        num_of_units = input_integer("Enter Number of Units: ")
        midterm_grade = input_float("Enter Midterm Grade: ")
        final_grade = input_float("Enter Final Grade: ")

        student.add_subject(subject_code, subject_description, num_of_units, midterm_grade, final_grade)

        another_subject = input("Do you want to add another subject? (y/n): ").lower()
        if another_subject != 'y':
            break

    # Save the student record to file
    with open('students.txt', 'a') as file:
        file.write(f"{student.student_number},{student.student_name}\n")
        for subject in student.subjects:
            file.write(f"{subject['subject_code']},{subject['subject_description']},{subject['num_of_units']},"
                       f"{subject['midterm_grade']},{subject['final_grade']},{subject['average_grade']},"
                       f"{subject['remarks']}\n")


# Method to load students from the file
def load_students():
    students = {}
    if os.path.exists('students.txt'):
        with open('students.txt', 'r') as file:
            lines = file.readlines()
            i = 0
            while i < len(lines):
                student_info = lines[i].strip().split(',')
                student_number = int(student_info[0])
                student_name = student_info[1]
                student = Student(student_number, student_name)

                # Load subjects
                i += 1
                while i < len(lines) and len(lines[i].strip().split(',')) == 7:
                    subject_info = lines[i].strip().split(',')
                    subject_code = subject_info[0]
                    subject_description = subject_info[1]
                    num_of_units = int(subject_info[2])
                    midterm_grade = float(subject_info[3])
                    final_grade = float(subject_info[4])
                    average_grade = float(subject_info[5])
                    remarks = subject_info[6]

                    student.add_subject(subject_code, subject_description, num_of_units, midterm_grade, final_grade)
                    i += 1

                students[student_number] = student
    return students


# Method to edit student information
def edit_student(students):
    student_number = input_integer("Enter Student Number to Edit: ")
    if student_number not in students:
        print("Student not found!")
        return

    student = students[student_number]
    print(f"Editing details for {student.student_name} (Student Number: {student.student_number})")

    student_name = input("Enter new Student Name: ")
    student.student_name = student_name

    # Edit subjects
    for i, subject in enumerate(student.subjects):
        print(f"\nEditing Subject {i + 1}: {subject['subject_code']}")
        subject_code = input("Enter new Subject Code: ")
        subject_description = input("Enter new Subject Description: ")
        num_of_units = input_integer("Enter new Number of Units: ")
        midterm_grade = input_float("Enter new Midterm Grade: ")
        final_grade = input_float("Enter new Final Grade: ")

        student.subjects[i] = {
            'subject_code': subject_code,
            'subject_description': subject_description,
            'num_of_units': num_of_units,
            'midterm_grade': midterm_grade,
            'final_grade': final_grade,
            'average_grade': student.calculate_average(midterm_grade, final_grade),
            'remarks': student.calculate_remarks(midterm_grade, final_grade)
        }

    # Save updated records to file
    with open('students.txt', 'w') as file:
        for student in students.values():
            file.write(f"{student.student_number},{student.student_name}\n")
            for subject in student.subjects:
                file.write(f"{subject['subject_code']},{subject['subject_description']},{subject['num_of_units']},"
                           f"{subject['midterm_grade']},{subject['final_grade']},{subject['average_grade']},"
                           f"{subject['remarks']}\n")


# Method to delete a student record
def delete_student(students):
    student_number = input_integer("Enter Student Number to Delete: ")
    if student_number not in students:
        print("Student not found!")
        return

    confirm = input(f"Are you sure you want to delete student {students[student_number].student_name}? (y/n): ").lower()
    if confirm == 'y':
        del students[student_number]
        # Save updated records to file
        with open('students.txt', 'w') as file:
            for student in students.values():
                file.write(f"{student.student_number},{student.student_name}\n")
                for subject in student.subjects:
                    file.write(f"{subject['subject_code']},{subject['subject_description']},{subject['num_of_units']},"
                               f"{subject['midterm_grade']},{subject['final_grade']},{subject['average_grade']},"
                               f"{subject['remarks']}\n")
        print(f"Student {student_number} deleted successfully.")


# Method to view student records
def view_students(students):
    if not students:
        print("No students to display.")
        return

    for student in students.values():
        student.display()


# Main Program
def main():
    students = load_students()

    while True:
        print("\n1. Add Student")
        print("2. Edit Student")
        print("3. Delete Student")
        print("4. View Students")
        print("5. Exit")
        choice = input_integer("\nEnter your choice: ")

        if choice == 1:
            add_student()
        elif choice == 2:
            edit_student(students)
        elif choice == 3:
            delete_student(students)
        elif choice == 4:
            view_students(students)
        elif choice == 5:
            print("Exiting the program.")
            break
        else:
            print("Invalid choice, please try again.")


if __name__ == "__main__":
    main()
