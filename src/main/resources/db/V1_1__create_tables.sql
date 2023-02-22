CREATE SCHEMA wash_my_car;

CREATE TABLE wash_my_car.schedule (
    id serial not null primary key,
    date date NOT NULL,
    user_id bigint NOT NULL,
    service_id bigint NOT NULL,
    slots integer[] NOT NULL
);

create sequence if not exists wash_my_car.schedule_id_seq owned by wash_my_car.schedule.id;
alter table wash_my_car.schedule alter column id set default nextval('wash_my_car.schedule_id_seq');

CREATE TABLE wash_my_car.service (
    id serial not null primary key,
    name character varying(1000),
    active boolean DEFAULT true NOT NULL,
    slots integer DEFAULT 1 NOT NULL
);

create sequence if not exists wash_my_car.service_id_seq owned by wash_my_car.service.id;
alter table wash_my_car.service alter column id set default nextval('wash_my_car.service_id_seq');

CREATE TABLE wash_my_car."user" (
    id serial not null primary key,
    name character varying NOT NULL,
    phone character varying(12) NOT NULL
);

create sequence if not exists wash_my_car.user_id_seq owned by wash_my_car.user.id;
alter table wash_my_car.user alter column id set default nextval('wash_my_car.user_id_seq');
