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
  --clean \
  --if-exists \
  xpense_2025-12-21.dump
```