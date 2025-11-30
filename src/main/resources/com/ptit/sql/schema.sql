-- Drop existing tables
DROP TABLE IF EXISTS public.student_and_sclass CASCADE;
DROP TABLE IF EXISTS public.sclass CASCADE;
DROP TABLE IF EXISTS public.student CASCADE;

CREATE TABLE public.student (
    id SERIAL PRIMARY KEY,
    sname VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    birthday DATE
);

CREATE TABLE public.sclass (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    monitor_id INTEGER,
    CONSTRAINT fk_sclass_monitor 
        FOREIGN KEY (monitor_id) 
        REFERENCES public.student(id) 
        ON DELETE SET NULL
);

CREATE TABLE public.student_and_sclass (
    sclass_id INTEGER NOT NULL,
    student_id INTEGER NOT NULL,
    PRIMARY KEY (sclass_id, student_id),
    CONSTRAINT fk_student_and_sclass_sclass 
        FOREIGN KEY (sclass_id) 
        REFERENCES public.sclass(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_student_and_sclass_student 
        FOREIGN KEY (student_id) 
        REFERENCES public.student(id) 
        ON DELETE CASCADE
);

CREATE INDEX idx_student_email ON public.student(email);
CREATE INDEX idx_sclass_monitor ON public.sclass(monitor_id);
CREATE INDEX idx_student_and_sclass_sclass ON public.student_and_sclass(sclass_id);
CREATE INDEX idx_student_and_sclass_student ON public.student_and_sclass(student_id);

