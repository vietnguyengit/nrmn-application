
    alter table if exists nrmn.error_check 
       drop constraint if exists FK_ERROR_STAGED_SURVEY;

    alter table if exists nrmn.staged_survey_job 
       drop constraint if exists PK_STAGED_JOB_UNIQUE;

    alter table if exists nrmn.staged_survey_job 
       drop constraint if exists PK_STAGED_JOB_STAGED_SURVEY;

    drop table if exists nrmn.error_check cascade;

    drop table if exists nrmn.staged_job cascade;

    drop table if exists nrmn.staged_survey cascade;

    drop table if exists nrmn.staged_survey_job cascade;

    create table nrmn.error_check (
       Message varchar(255) not null,
        jobId varchar(255) not null,
        surveyId int8 not null,
        column_target varchar(255),
        type int4,
        row_id int8,
        primary key (Message, jobId, surveyId)
    );

    create table nrmn.staged_job (
       file_id varchar(255) not null,
        job_attributes uuid,
        source varchar(255),
        status varchar(255),
        primary key (file_id)
    );

    create table nrmn.staged_survey (
       id  SERIAL PRIMARY KEY,
        common_name varchar(255),
        L5 int4,
        L95 int4,
        PQs int4,
        block int4,
        buddy varchar(255),
        code varchar(255),
        date date,
        depth float8,
        direction varchar(255),
        diver varchar(255),
        inverts int4,
        is_invert_Sizing boolean,
        latitude float8,
        longitude float8,
        m2_invert_sizing_species boolean,
        measureValue json,
        method int4,
        site_name varchar(255),
        site_no varchar(255),
        species varchar(255),
        time float8,
        total int4,
        vis int4
    );

    create table nrmn.staged_survey_job (
       stagedJob_file_id varchar(255),
        id int8 not null,
        primary key (id)
    );

    alter table if exists nrmn.error_check 
       add constraint FK_ERROR_STAGED_SURVEY 
       foreign key (row_id) 
       references nrmn.staged_survey;

    alter table if exists nrmn.staged_survey_job 
       add constraint PK_STAGED_JOB_UNIQUE 
       foreign key (stagedJob_file_id) 
       references nrmn.staged_job;

    alter table if exists nrmn.staged_survey_job 
       add constraint PK_STAGED_JOB_STAGED_SURVEY 
       foreign key (id) 
       references nrmn.staged_survey;
