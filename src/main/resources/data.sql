-- Insert categories
INSERT INTO categories (id, name) VALUES 
(1, 'Development'),
(2, 'Design'),
(3, 'Business'),
(4, 'Marketing'),
(5, 'Data Science')
ON CONFLICT (id) DO NOTHING;

-- Insert teachers
INSERT INTO teachers (id, name, email, department, experience_years) VALUES 
(1, 'Alice Johnson', 'alice@example.com', 'Software Engineering', 5),
(2, 'Bob Smith', 'bob@example.com', 'Design', 8),
(3, 'Carol White', 'carol@example.com', 'Business', 10),
(4, 'David Brown', 'david@example.com', 'Data Science', 7),
(5, 'Eva Green', 'eva@example.com', 'Marketing', 6)
ON CONFLICT (id) DO NOTHING;

-- Insert students
INSERT INTO students (id, name, email, enrollment_date) VALUES 
(1, 'Vladimir', 'vladimir@example.com', '2026-01-15'),
(2, 'Maria', 'maria@example.com', '2026-02-20'),
(3, 'Ivan', 'ivan@example.com', '2026-03-10'),
(4, 'Anna', 'anna@example.com', '2026-04-01'),
(5, 'Petr', 'petr@example.com', '2026-04-05')
ON CONFLICT (id) DO NOTHING;

-- Insert courses
INSERT INTO courses (id, title, description, price, duration_weeks, category_id, teacher_id) VALUES 
(1, 'Java Masterclass', 'Complete Java programming from basics to advanced', 199.99, 12, 1, 1),
(2, 'React.js Complete', 'Modern React with hooks and state management', 249.99, 10, 1, 1),
(3, 'UI/UX Design Fundamentals', 'Learn user interface and experience design', 179.99, 8, 2, 2),
(4, 'Business Strategy 101', 'Essential business strategies for startups', 299.99, 6, 3, 3),
(5, 'Python for Data Science', 'Data analysis and machine learning with Python', 349.99, 14, 5, 4),
(6, 'Digital Marketing Pro', 'Complete digital marketing course', 199.99, 8, 5, 5)
ON CONFLICT (id) DO NOTHING;

-- Insert lessons
INSERT INTO lessons (id, title, content, duration_minutes, video_url, course_id) VALUES 
(1, 'Introduction to Java', 'Java basics and setup', 45, 'https://www.youtube.com/watch?v=Hl-zzrqQoSE', 1),
(2, 'Object-Oriented Programming', 'OOP concepts in Java', 60, 'https://www.youtube.com/watch?v=pTB0EiLX38M', 1),
(3, 'Java Collections', 'Lists, sets, and maps', 50, 'https://www.youtube.com/watch?v=57SNyQk_aJU', 1),
(4, 'React Basics', 'Components and props', 40, 'https://www.youtube.com/watch?v=SqcY0GlETPk', 2),
(5, 'State Management', 'Redux and Context API', 55, 'https://www.youtube.com/watch?v=CVClUw8-4rU', 2),
(6, 'Design Principles', 'Color theory and typography', 35, 'https://www.youtube.com/watch?v=A51CRNEMvV0', 3),
(7, 'User Research', 'Interviews and surveys', 45, 'https://www.youtube.com/watch?v=0LQXpRpT2Qk', 3),
(8, 'Business Models', 'Canvas and strategies', 50, 'https://www.youtube.com/watch?v=5oU2dN4k8bM', 4),
(9, 'Python Basics', 'Variables and data types', 40, 'https://www.youtube.com/watch?v=rfscVS0vtbw', 5),
(10, 'Machine Learning Intro', 'ML fundamentals', 60, 'https://www.youtube.com/watch?v=ukzFI9rgwfU', 5)
ON CONFLICT (id) DO NOTHING;

-- Enroll students in courses
INSERT INTO course_students (course_id, student_id) VALUES 
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 4), (2, 5),
(3, 2), (3, 3),
(4, 4), (4, 5),
(5, 1), (5, 3), (5, 5),
(6, 2), (6, 4)
ON CONFLICT DO NOTHING;
