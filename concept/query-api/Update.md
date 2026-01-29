1. Transactions should be bound to a connection, they should not be got from the Database itself
2. Transactions should support nested transactions (Transaction.current(), Transaction.requireActive(), Transaction.isInTransaction())
3. Concept for functions in the sql queries, how to handle functions from extensions like TimeScaleDB
4. what fetch methods should exist in sync and async variants (fetch, fetchOne, fetchFirst, count, exists, ...?)
5. Generated record class (currently named TupleX) should be Row, where the user configures how many fields are in the row (how many record should be generated)
   only for select queries not for inserts/updates/deletes and selectAll (which always return the full record of the column, like User)
6. relationship and one-to-many and many-to-many handling is currently worse defined
   1. there should not be two not be two fields in the record one for the id and one for the related object (nullable)
      there should be a key record SqlKey<UUID, Address> (one-to-one) and SqlKey<UUID, List<Address>> (one-to-many)
      not sure how to handle many-to-many relationships here
7. audit should be better defined (createdAt, createdBy, updatedAt, updatedBy) the column names should be configurable (more config options on the Table level?)
8. how is the table actually defined in the yaml schema file? the current documentation is not sufficient
9. for sql dialect we should have a better concept how to extend/customize it (for example for TimeScaleDB or other extensions)
   focus should be the most common use cases (PostgreSQL, MySQL, SQLite) with some popular extensions (TimeScaleDB, MariaDB, ...)
   1. i need a detailed comparison of the dialects and their differences
10. the yaml schema should allow multiple tables and entities in one file
