alter table moviefile add newsize bigint not null default 0;
UPDATE moviefile set newsize=size;
alter table moviefile drop column size;
alter table moviefile rename newsize to size;
