create table if not exists widgets (
    id varchar(255) not null,
    updated_at bigint not null,
    x1_coordinate integer not null,
    x2_coordinate integer not null,
    y1_coordinate integer not null,
    y2_coordinate integer not null,
    z_index integer not null,
    primary key (id)
);
create index if not exists filtering_index on widgets (x1_coordinate, y1_coordinate, x2_coordinate, y2_coordinate);
create index if not exists unique_index__z_index on widgets (z_index);

