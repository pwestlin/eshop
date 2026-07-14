-- Customers
insert into customers(id,name,email)
values('d9538d50-8976-4dea-9b5f-96ecf5bbafc5','Sune Son', 'sune@son.nu')
ON CONFLICT (id) DO NOTHING;

insert into customers(id,name,email)
values('31eea1b4-f80b-43f6-9a6a-571430c88cd1','Koma Klasse', 'koma@klasse.nu')
ON CONFLICT (id) DO NOTHING;


-- Products
insert into products(id,name,description,price)
values(1,'Muffler', 'A really nice muffler!',5)
ON CONFLICT (id) DO NOTHING;

insert into products(id,name,description,price)
values(2,'Brake caliper', 'A really nice brake caliper!',42)
ON CONFLICT (id) DO NOTHING;

insert into products(id,name,description,price)
values(3,'Tyre', 'A really nice tyre!',69)
ON CONFLICT (id) DO NOTHING;