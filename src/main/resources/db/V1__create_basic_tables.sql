create table checksum (
	id serial primary key,
	md5sum varchar(50) not null
);

create table moviefile (
	id serial primary key,
	path varchar(500) not null,
	checksum int not null references checksum
);
