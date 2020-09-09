
alter table if exists nrmn.sec_user_aud
    drop constraint if exists FK_SEC_USER_AUD_REV;

alter table if exists nrmn.sec_user_sec_role
    drop constraint if exists FK_ROLE_USER_SEC;

alter table if exists nrmn.sec_user
    drop constraint if exists UNIQUE_EMAIL;


alter table if exists nrmn.sec_user_sec_role
    drop constraint if exists FK_USER_SEC_ROLE;

drop table if exists nrmn.REVINFO cascade;

drop table if exists nrmn.sec_role cascade;

drop table if exists nrmn.sec_user cascade;

drop table if exists nrmn.sec_user_aud cascade;

drop table if exists nrmn.sec_user_sec_role cascade;

drop sequence if exists nrmn.hibernate_sequence;

drop sequence if exists nrmn.role_id_seq;

drop sequence if exists nrmn.user_id_seq;
create sequence nrmn.hibernate_sequence start 1 increment 1;
create sequence nrmn.role_id_seq start 1 increment 1;
create sequence nrmn.user_id_seq start 1 increment 1;

create table nrmn.REVINFO (
                              REV int4 not null,
                              REVTSTMP int8,
                              primary key (REV)
);

create table nrmn.sec_role (
                               id int8 not null,
                               name varchar(255) not null,
                               version int4 not null,
                               primary key (id)
);

create table nrmn.sec_user (
                               id int4 not null,
                               email_address varchar(255) not null,
                               full_name varchar(255),
                               hashed_password varchar(255),
                               status varchar(255) not null,
                               version int4 not null,
                               primary key (id)
);

create table nrmn.sec_user_aud (
                                   id int4 not null,
                                   REV int4 not null,
                                   REVTYPE int2,
                                   email_address varchar(255),
                                   email_MOD boolean,
                                   full_name varchar(255),
                                   fullName_MOD boolean,
                                   hashed_password varchar(255),
                                   hashedPassword_MOD boolean,
                                   status varchar(255),
                                   status_MOD boolean,
                                   primary key (id, REV)
);

create table nrmn.sec_user_sec_role (
                                        sec_user_id int4 not null,
                                        sec_role_id int8 not null,
                                        primary key (sec_user_id, sec_role_id)
);

alter table if exists nrmn.sec_user
    add constraint UNIQUE_EMAIL unique (email_address);

alter table if exists nrmn.sec_user_aud
    add constraint FK_SEC_USER_AUD_REV
        foreign key (REV)
            references nrmn.REVINFO;

alter table if exists nrmn.sec_user_sec_role
    add constraint FK_ROLE_USER_SEC
        foreign key (sec_role_id)
            references nrmn.sec_role;

alter table if exists nrmn.sec_user_sec_role
    add constraint FK_USER_SEC_ROLE
        foreign key (sec_user_id)
            references nrmn.sec_user;