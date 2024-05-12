create table if not exists upload_statistics
(
    id int generated always as identity
        primary key,
    ip_address varchar(255) not null,
    upload_date date not null,
    file_count int not null
);