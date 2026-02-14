## Taking the backup of database

```
pg_dump \
  -h localhost \
  -p 5432 \
  -U xpense_admin \
  -F c \
  -Z 9 \
  -f xpense_backup_$(date +%F).dump \
  xpense_tracker
```


## Restoring the backup to the same database

```
pg_restore \
  -h localhost \
  -p 5432 \
  -U xpense_admin \
  -d xpense_tracker \
  xpense_2025-12-21.dump
```

## Creating a new user

### Connect to the psql in the container

`docker exec -it <container_name> psql -U xpense_admin -d postgres`

### List the active users(roles)

`\du` - This should list the users in the database. It should list the below
```
  Role name   |                         Attributes                         
--------------+------------------------------------------------------------
 xpense_admin | Superuser, Create role, Create DB, Replication, Bypass RLS
```

### Create the new user

`CREATE ROLE restore_user WITH LOGIN PASSWORD 'restoreUser1234';`

### Try listing user again

`\du`

This should list below

```
  Role name   |                         Attributes                         
--------------+------------------------------------------------------------
 restore_user | 
 xpense_admin | Superuser, Create role, Create DB, Replication, Bypass RLS
```

### Now grant access to the database

`GRANT CONNECT ON DATABASE test_db TO restore_user;`

### Connect to the new database

`\c test_db`

### Now Grant access to the schema

`GRANT USAGE ON SCHEMA public TO restore_user;`

```
GRANT SELECT, INSERT, UPDATE, DELETE
ON ALL TABLES IN SCHEMA public
TO restore_user;
```

```
GRANT USAGE, SELECT
ON ALL SEQUENCES IN SCHEMA public
TO restore_user;
```