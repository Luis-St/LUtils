# Most identical to SQL
Idea of a fluent interface for building SQL queries in a more readable and chainable way.

## Dialect tags
Calls that are not distinct SQL features but the same operation spelled differently per vendor
are tagged in the listings below:

- `// <group>#<dialect>` — merge candidate: every call sharing a `<group>` collapses into one
  builder call plus an enum/flag, with `<dialect>` naming whose syntax the line is written in.
- `// #<dialect>` — dialect-specific, but **not** a merge candidate: no equivalent elsewhere, or
  emulating it means rewriting the query rather than changing the rendering. Keep as its own call
  and gate it behind a capability check.
- Untagged lines are dialect-neutral.

`ifExists()` / `ifNotExists()` and `cascade()` are cross-cutting flags rather than per-line
variants, so they are not tagged individually: `IF EXISTS` is absent in Oracle and older SQL Server
(needs a catalog-lookup wrapper), and `CASCADE` is PostgreSQL/Oracle only.

## DML — select
```
DB.select(<columns>).from(<table>);
DB.select(<columns>).from(<table>).as(<alias>);
DB.select(<columns>).from(<table>).where(<condition>);
DB.select(<columns>).from(<table>).where(<condition>).orderBy(<columns>, <order>);
DB.select(<columns>).from(<table>).where(<condition>).orderBy(<columns>, <order>).nullsFirst();                  // #standard
DB.select(<columns>).from(<table>).where(<condition>).orderBy(<columns>, <order>).limit(<count>);                // limit#postgres
DB.select(<columns>).from(<table>).where(<condition>).orderBy(<columns>, <order>).limit(<count>).offset(<count>);// limit#postgres
DB.select(<columns>).from(<table>).where(<condition>).orderBy(<columns>, <order>).fetchFirst(<count>).withTies();// limit#standard
DB.select(<columns>).from(<table>).where(<condition>).groupBy(<columns>);
DB.select(<columns>).from(<table>).where(<condition>).groupBy(<columns>).having(<condition>);
DB.select(<columns>).from(<table>).where(<condition>).groupByRollup(<columns>);                                  // groupBy#standard
DB.select(<columns>).from(<table>).where(<condition>).groupByCube(<columns>);                                    // groupBy#standard
DB.select(<columns>).from(<table>).where(<condition>).groupBySets(<column_sets>);                                // groupBy#standard
DB.selectDistinct(<columns>).from(<table>).where(<condition>);
DB.selectDistinctOn(<columns>).from(<table>).orderBy(<columns>, <order>);                                        // #postgres
DB.select(<columns>).from(<table>).tableSample(<method>, <percentage>).repeatable(<seed>);                       // #standard
DB.select(<columns>).from(<table>).union(<query>);                                                               // setOp#standard
DB.select(<columns>).from(<table>).unionAll(<query>);                                                            // setOp#standard
DB.select(<columns>).from(<table>).intersect(<query>);                                                           // setOp#standard
DB.select(<columns>).from(<table>).intersectAll(<query>);                                                        // setOp#standard
DB.select(<columns>).from(<table>).except(<query>);                                                              // setOp#standard
DB.select(<columns>).from(<table>).exceptAll(<query>);                                                           // setOp#standard
DB.select(<columns>).into(<table>).from(<table>).where(<condition>);                                             // ctas#mssql
```

## DML — insert, update, delete
```
DB.insert(<columns>).into(<table>).values(<values>);
DB.insert(<columns>).into(<table>).values(<values>).returning(<columns>);                                        // returning#postgres
DB.insert(<columns>).into(<table>).select(<columns>).from(<table>).where(<condition>);
DB.insert().into(<table>).defaultValues();                                                                       // defaultValues#standard
DB.insert(<columns>).into(<table>).overridingSystemValue().values(<values>);                                     // identityOverride#standard
DB.update(<table>).set(<column_values>).where(<condition>);
DB.update(<table>).as(<alias>).set(<column_values>).where(<condition>);
DB.update(<table>).set(<column_values>).from(<table>).where(<condition>);                                        // correlatedDml#postgres
DB.update(<table>).set(<column_values>).where(<condition>).returning(<columns>);                                 // returning#postgres
DB.update(<table>).setFrom(<columns>, <query>).where(<condition>);                                               // set#postgres
DB.delete().from(<table>).where(<condition>);
DB.delete().from(<table>).using(<table>).where(<condition>);                                                     // correlatedDml#postgres
DB.delete().from(<table>).where(<condition>).returning(<columns>);                                               // returning#postgres
DB.truncate(<table>);
DB.truncate(<table>).restartIdentity();                                                                          // truncateIdentity#postgres
DB.truncate(<table>).cascade();                                                                                  // #postgres
DB.upsert(<columns>).into(<table>).values(<values>).onConflict(<columns>).doUpdate(<column_values>);              // upsert#postgres
DB.upsert(<columns>).into(<table>).values(<values>).onConflict(<columns>).where(<condition>).doUpdate(<column_values>).where(<condition>); // upsert#postgres
DB.upsert(<columns>).into(<table>).values(<values>).onConflictOnConstraint(<name>).doNothing();                   // upsert#postgres
DB.replace(<columns>).into(<table>).values(<values>);                                                            // upsert#mysql (delete+insert, not a true upsert)
DB.merge(<table>).using(<source>).on(<condition>).whenMatched().thenUpdate(<column_values>);                      // upsert#standard
DB.merge(<table>).using(<source>).on(<condition>).whenMatched().and(<condition>).thenDelete();                    // merge#standard
DB.merge(<table>).using(<source>).on(<condition>).whenNotMatched().thenInsert(<columns>, <values>);               // upsert#standard
DB.merge(<table>).using(<source>).on(<condition>).whenNotMatchedBySource().thenDelete();                          // merge#standard
DB.call(<procedure>).with(<arguments>);                                                                          // call#standard
DB.copy(<table>).columns(<columns>).from(<source>).format(<format>).header();                                    // bulk#postgres
DB.copy(<query>).to(<target>).format(<format>).delimiter(<char>);                                                // bulk#postgres
```

## Joins
```
DB.select(<columns>).from(<table>).join(<table>).on(<condition>);                                                // join#standard
DB.select(<columns>).from(<table>).innerJoin(<table>).on(<condition>);                                           // join#standard
DB.select(<columns>).from(<table>).leftJoin(<table>).on(<condition>);                                            // join#standard
DB.select(<columns>).from(<table>).rightJoin(<table>).on(<condition>);                                           // join#standard
DB.select(<columns>).from(<table>).fullJoin(<table>).on(<condition>);                                            // join#standard
DB.select(<columns>).from(<table>).crossJoin(<table>);                                                           // join#standard
DB.select(<columns>).from(<table>).naturalJoin(<table>);                                                         // join#standard
DB.select(<columns>).from(<table>).leftJoin(<table>).using(<columns>);                                           // join#standard
DB.select(<columns>).from(<table>).join(<table>).as(<alias>).on(<condition>);                                    // join#standard
DB.select(<columns>).from(<table>).join(<query>).as(<alias>).on(<condition>);                                    // join#standard
DB.select(<columns>).from(<table>).crossJoinLateral(<query>).as(<alias>);                                        // join#postgres
DB.select(<columns>).from(<table>).leftJoinLateral(<query>).as(<alias>).on(<condition>);                         // join#postgres
DB.select(<columns>).from(<table>).join(<table>).on(<condition>).join(<table>).on(<condition>);                   // join#standard
```

## Common table expressions / recursive
```
DB.with(<name>).as(<query>).select(<columns>).from(<name>);
DB.with(<name>).columns(<columns>).as(<query>).select(<columns>).from(<name>);
DB.with(<name>).as(<query>).and(<name>).as(<query>).select(<columns>).from(<name>);
DB.with(<name>).as(<query>).materialized().select(<columns>).from(<name>);                                       // cteHint#postgres
DB.with(<name>).as(<query>).notMaterialized().select(<columns>).from(<name>);                                    // cteHint#postgres
DB.withRecursive(<name>).columns(<columns>).as(<anchor_query>).unionAll(<recursive_query>).select(<columns>).from(<name>);              // #standard
DB.withRecursive(<name>).columns(<columns>).as(<anchor_query>).union(<recursive_query>).select(<columns>).from(<name>).where(<condition>); // #standard
DB.withRecursive(<name>).columns(<columns>).as(<anchor_query>).unionAll(<recursive_query>).searchDepthFirst(<columns>).set(<column>).select(<columns>).from(<name>); // cteSearch#standard
DB.withRecursive(<name>).columns(<columns>).as(<anchor_query>).unionAll(<recursive_query>).cycle(<columns>).set(<column>).using(<column>).select(<columns>).from(<name>); // cteSearch#standard
DB.with(<name>).as(DB.insert(<columns>).into(<table>).values(<values>).returning(<columns>)).select(<columns>).from(<name>);            // #postgres
DB.with(<name>).as(<query>).update(<table>).set(<column_values>).where(<condition>);                             // #postgres
DB.with(<name>).as(<query>).delete().from(<table>).where(<condition>);                                           // #postgres
```

## Window functions
```
DB.select(Fn.rowNumber().over()).from(<table>);
DB.select(Fn.rank().over().partitionBy(<columns>)).from(<table>);
DB.select(Fn.denseRank().over().partitionBy(<columns>).orderBy(<columns>, <order>)).from(<table>);
DB.select(Fn.sum(<column>).over().partitionBy(<columns>).orderBy(<columns>, <order>).rows().between(<start>, <end>)).from(<table>);     // frame#standard
DB.select(Fn.avg(<column>).over().orderBy(<columns>, <order>).range().between(<start>, <end>).excludeCurrentRow()).from(<table>);       // frame#standard
DB.select(Fn.count(<column>).over().orderBy(<columns>, <order>).groups().between(<start>, <end>)).from(<table>);                        // frame#standard
DB.select(Fn.lag(<column>, <offset>, <default>).over().partitionBy(<columns>).orderBy(<columns>, <order>)).from(<table>);
DB.select(Fn.lead(<column>, <offset>).over().orderBy(<columns>, <order>)).from(<table>);
DB.select(Fn.firstValue(<column>).over().partitionBy(<columns>)).from(<table>);
DB.select(Fn.lastValue(<column>).over().partitionBy(<columns>)).from(<table>);
DB.select(Fn.nthValue(<column>, <n>).over().partitionBy(<columns>)).from(<table>);
DB.select(Fn.ntile(<n>).over().orderBy(<columns>, <order>)).from(<table>);
DB.select(Fn.percentRank().over().orderBy(<columns>, <order>)).from(<table>);
DB.select(Fn.cumeDist().over().orderBy(<columns>, <order>)).from(<table>);
DB.select(Fn.sum(<column>).filter(<condition>).over(<name>)).from(<table>).window(<name>).partitionBy(<columns>).orderBy(<columns>, <order>); // filter#postgres
DB.select(Fn.percentileCont(<fraction>).withinGroup().orderBy(<columns>, <order>)).from(<table>);                 // #standard
DB.select(Fn.arrayAgg(<column>).orderBy(<columns>, <order>).filter(<condition>)).from(<table>).groupBy(<columns>);// filter#postgres, aggName#postgres
```

## Subqueries
```
DB.select(<columns>).from(<table>).where(Cond.column(<column>).eq(<query>));                  // scalar
DB.select(<columns>).from(<table>).where(Cond.column(<column>).in(<query>));
DB.select(<columns>).from(<table>).where(Cond.column(<column>).notIn(<query>));
DB.select(<columns>).from(<table>).where(Cond.exists(<query>));
DB.select(<columns>).from(<table>).where(Cond.notExists(<query>));
DB.select(<columns>).from(<table>).where(Cond.column(<column>).gt(Cond.any(<query>)));
DB.select(<columns>).from(<table>).where(Cond.column(<column>).lt(Cond.all(<query>)));
DB.select(<columns>).from(<table>).where(Cond.columns(<columns>).eq(<query>));                // row constructor, #standard
DB.select(<query>).from(<table>);                                                             // scalar in projection
DB.select(<columns>).from(<query>).as(<alias>);                                               // derived table
DB.select(<columns>).from(<table>).groupBy(<columns>).having(Cond.aggregate(<function>).gt(<query>));
DB.update(<table>).set(<column>, <query>).where(<condition>);
```

## Locking
```
DB.select(<columns>).from(<table>).where(<condition>).forUpdate();                                               // lockMode#standard
DB.select(<columns>).from(<table>).where(<condition>).forUpdate().of(<tables>);                                  // lockMode#standard
DB.select(<columns>).from(<table>).where(<condition>).forUpdate().noWait();                                      // lockMode#standard
DB.select(<columns>).from(<table>).where(<condition>).forUpdate().skipLocked();                                  // lockMode#standard
DB.select(<columns>).from(<table>).where(<condition>).forNoKeyUpdate();                                          // lockMode#postgres
DB.select(<columns>).from(<table>).where(<condition>).forShare();                                                // lockMode#standard
DB.select(<columns>).from(<table>).where(<condition>).forKeyShare();                                             // lockMode#postgres
DB.lock(<table>).inMode(<lock_mode>);                                                                            // lockTable#postgres
DB.lock(<table>).inMode(<lock_mode>).noWait();                                                                   // lockTable#postgres
DB.advisoryLock(<key>);                                                                                          // advisoryLock#postgres
DB.advisoryLockShared(<key>);                                                                                    // advisoryLock#postgres
DB.tryAdvisoryLock(<key>);                                                                                       // advisoryLock#postgres
DB.advisoryUnlock(<key>);                                                                                        // advisoryLock#postgres
DB.advisoryUnlockAll();                                                                                          // advisoryLock#postgres
```

## Transactions
```
DB.begin();                                                                                                      // txn#standard
DB.begin().isolationLevel(<level>);                                                                              // txn#standard
DB.begin().isolationLevel(<level>).readWrite();                                                                  // txn#standard
DB.begin().readOnly().deferrable();                                                                              // txn#postgres
DB.commit();
DB.commit().andChain();                                                                                          // txn#standard
DB.rollback();
DB.rollback().andChain();                                                                                        // txn#standard
DB.savepoint(<name>);                                                                                            // savepoint#standard
DB.rollbackTo(<name>);                                                                                           // savepoint#standard
DB.releaseSavepoint(<name>);                                                                                     // savepoint#standard
DB.setTransaction().isolationLevel(<level>);                                                                     // txn#standard
DB.setSessionCharacteristics().isolationLevel(<level>).readOnly();                                               // txn#standard
DB.setConstraints(<names>).deferred();                                                                           // #postgres
DB.setConstraintsAll().immediate();                                                                              // #postgres
DB.prepareTransaction(<id>);                                  // twoPhase#postgres, two-phase commit
DB.commitPrepared(<id>);                                      // twoPhase#postgres
DB.rollbackPrepared(<id>);                                    // twoPhase#postgres
DB.transaction().isolationLevel(<level>).run(<block>);        // scoped, auto commit/rollback
DB.transaction().readOnly().run(<block>);
```

## Prepared statements, cursors, batches
```
DB.prepare(<name>).parameters(<types>).as(<query>);                                                              // serverPrepare#postgres
DB.executePrepared(<name>).with(<values>);                                                                       // serverPrepare#postgres
DB.deallocate(<name>);                                                                                           // serverPrepare#postgres
DB.deallocateAll();                                                                                              // serverPrepare#postgres
DB.declare(<name>).cursorFor(<query>);                                                                           // cursor#standard
DB.declare(<name>).binary().insensitive().scroll().cursorFor(<query>).withHold();                                // cursor#postgres
DB.fetch(<count>).from(<name>);                                                                                  // fetch#standard
DB.fetchForward(<count>).from(<name>);                                                                           // fetch#standard
DB.fetchBackward(<count>).from(<name>);                                                                          // fetch#standard
DB.fetchAbsolute(<position>).from(<name>);                                                                       // fetch#standard
DB.move(<count>).in(<name>);                                                                                     // fetch#postgres
DB.closeCursor(<name>);                                                                                          // cursor#standard
DB.update(<table>).set(<column_values>).whereCurrentOf(<cursor>);                                                // cursor#standard
DB.delete().from(<table>).whereCurrentOf(<cursor>);                                                              // cursor#standard
DB.batch(<statements>);
DB.insert(<columns>).into(<table>).valuesBatch(<value_rows>);
```

## DDL
```
DB.create().database(<name>).ifNotExists().encoding(<encoding>).owner(<role>);                                   // createDatabase#postgres
DB.create().schema(<name>).ifNotExists().authorization(<role>);                                                  // #standard
DB.create().table(<name>).columns(<column_definitions>);
DB.create().table(<name>).ifNotExists().columns(<column_definitions>).constraints(<constraints>);
DB.create().table(<name>).columns(<column_definitions>).inherits(<tables>);                                      // #postgres
DB.create().table(<name>).columns(<column_definitions>).partitionBy(<strategy>, <columns>);                      // partition#postgres
DB.create().table(<name>).partitionOf(<table>).forValuesFrom(<from>).to(<to>);                                   // partition#postgres
DB.create().table(<name>).partitionOf(<table>).forValuesIn(<values>);                                            // partition#postgres
DB.create().table(<name>).partitionOf(<table>).defaultPartition();                                               // partition#postgres
DB.create().table(<name>).like(<table>).includingAll();                                                          // #postgres
DB.create().table(<name>).as(<query>).withNoData();                                                              // ctas#postgres
DB.create().temporaryTable(<name>).columns(<column_definitions>).onCommitDrop();                                 // createTable#standard
DB.create().unloggedTable(<name>).columns(<column_definitions>);                                                 // createTable#postgres
DB.create().view(<name>).columns(<columns>).as(<query>).withCheckOption();                                       // createView#standard
DB.create().orReplaceView(<name>).as(<query>);                                                                   // createView#postgres
DB.create().recursiveView(<name>).columns(<columns>).as(<query>);                                                // createView#standard
DB.create().materializedView(<name>).as(<query>).withData();                                                     // createView#postgres
DB.create().index(<name>).on(<table>).columns(<columns>, <order>).where(<condition>);                            // createIndex#standard
DB.create().index(<name>).on(<table>).using(<method>).columns(<columns>).include(<columns>);                     // createIndex#standard
DB.create().index(<name>).on(<table>).expression(<expression>);                                                  // createIndex#standard
DB.create().uniqueIndex(<name>).on(<table>).columns(<columns>).nullsNotDistinct();                               // createIndex#postgres
DB.create().indexConcurrently(<name>).on(<table>).columns(<columns>);                                            // createIndex#postgres
DB.create().sequence(<name>).startWith(<value>).incrementBy(<value>).minValue(<value>).maxValue(<value>).cycle().ownedBy(<table>, <column>); // #standard
DB.create().trigger(<name>).before(<events>).on(<table>).forEachRow().when(<condition>).execute(<function>);      // createTrigger#standard
DB.create().trigger(<name>).after(<events>).on(<table>).forEachStatement().execute(<function>);                  // createTrigger#standard
DB.create().trigger(<name>).insteadOf(<events>).on(<view>).forEachRow().execute(<function>);                     // createTrigger#standard
DB.create().constraintTrigger(<name>).after(<events>).on(<table>).deferrable().initiallyDeferred().forEachRow().execute(<function>); // createTrigger#postgres
DB.create().function(<name>).parameters(<parameters>).returns(<type>).language(<language>).immutable().as(<body>);              // createRoutine#postgres
DB.create().orReplaceFunction(<name>).parameters(<parameters>).returnsTable(<column_definitions>).language(<language>).as(<body>); // createRoutine#postgres
DB.create().procedure(<name>).parameters(<parameters>).language(<language>).as(<body>);                          // createRoutine#postgres
DB.create().aggregate(<name>).parameters(<parameters>).stateFunction(<function>).stateType(<type>).finalFunction(<function>); // #postgres
DB.create().type(<name>).asEnum(<values>);                                                                       // #postgres
DB.create().type(<name>).asComposite(<column_definitions>);                                                      // #postgres
DB.create().type(<name>).asRange(<subtype>);                                                                     // #postgres
DB.create().domain(<name>).as(<type>).notNull().defaultValue(<value>).check(<condition>);                        // #postgres
DB.create().role(<name>).withPassword(<password>).login().createDb();                                            // createRole#standard
DB.create().user(<name>).withPassword(<password>).inRole(<roles>);                                               // createRole#postgres
DB.create().extension(<name>).ifNotExists().schema(<schema>);                                                    // #postgres
DB.create().collation(<name>).locale(<locale>);                                                                  // #postgres
DB.create().rule(<name>).as().on(<event>).to(<table>).doInstead(<query>);                                        // #postgres
DB.create().policy(<name>).on(<table>).forCommand(<command>).to(<roles>).using(<condition>).withCheck(<condition>); // #postgres

DB.alter().table(<table>).addColumn(<column_definition>).ifNotExists();
DB.alter().table(<table>).dropColumn(<column>).ifExists().cascade();
DB.alter().table(<table>).renameColumn(<column>).to(<name>);                                                     // rename#standard
DB.alter().table(<table>).alterColumn(<column>).setType(<type>).using(<expression>);                             // alterColumn#postgres
DB.alter().table(<table>).alterColumn(<column>).setDefault(<value>);                                             // alterColumn#postgres
DB.alter().table(<table>).alterColumn(<column>).dropDefault();                                                   // alterColumn#postgres
DB.alter().table(<table>).alterColumn(<column>).setNotNull();                                                    // alterColumn#postgres
DB.alter().table(<table>).alterColumn(<column>).dropNotNull();                                                   // alterColumn#postgres
DB.alter().table(<table>).alterColumn(<column>).addGeneratedAsIdentity().startWith(<value>);                     // alterColumn#standard
DB.alter().table(<table>).alterColumn(<column>).dropIdentity().ifExists();                                       // alterColumn#standard
DB.alter().table(<table>).addConstraint(<constraint>).notValid();                                                // #postgres
DB.alter().table(<table>).validateConstraint(<name>);                                                            // #postgres
DB.alter().table(<table>).dropConstraint(<name>).ifExists().cascade();
DB.alter().table(<table>).renameConstraint(<name>).to(<name>);                                                   // rename#standard
DB.alter().table(<table>).attachPartition(<table>).forValuesFrom(<from>).to(<to>);                               // partition#postgres
DB.alter().table(<table>).detachPartition(<table>).concurrently();                                               // partition#postgres
DB.alter().table(<table>).enableRowLevelSecurity();                                                              // #postgres
DB.alter().table(<table>).disableTrigger(<name>);                                                                // #standard
DB.alter().table(<table>).renameTo(<name>);                                                                      // rename#standard
DB.alter().table(<table>).setSchema(<schema>);                                                                   // #postgres
DB.alter().table(<table>).owner(<role>);                                                                         // #postgres
DB.alter().table(<table>).setTablespace(<tablespace>);                                                           // #postgres
DB.alter().view(<name>).renameTo(<name>);                                                                        // rename#standard
DB.alter().materializedView(<name>).setSchema(<schema>);                                                         // #postgres
DB.alter().index(<name>).renameTo(<name>);                                                                       // rename#standard
DB.alter().sequence(<name>).restartWith(<value>).incrementBy(<value>);                                           // #standard
DB.alter().schema(<name>).renameTo(<name>);                                                                      // rename#standard
DB.alter().database(<name>).renameTo(<name>);                                                                    // rename#standard
DB.alter().type(<name>).addValue(<value>).after(<value>);                                                        // #postgres
DB.alter().type(<name>).renameValue(<value>).to(<value>);                                                        // #postgres
DB.alter().domain(<name>).addConstraint(<constraint>);                                                           // #postgres
DB.alter().function(<name>).parameters(<parameters>).renameTo(<name>);                                           // rename#postgres
DB.alter().role(<name>).withPassword(<password>).validUntil(<timestamp>);                                        // #standard
DB.alter().policy(<name>).on(<table>).using(<condition>);                                                        // #postgres

DB.drop().database(<name>).ifExists();
DB.drop().schema(<name>).ifExists().cascade();
DB.drop().table(<names>).ifExists().cascade();
DB.drop().view(<names>).ifExists().cascade();
DB.drop().materializedView(<name>).ifExists().cascade();                                                         // #postgres
DB.drop().index(<name>).ifExists().concurrently().cascade();                                                     // dropIndex#postgres
DB.drop().sequence(<name>).ifExists().cascade();
DB.drop().trigger(<name>).on(<table>).ifExists().cascade();
DB.drop().function(<name>).parameters(<parameters>).ifExists().cascade();
DB.drop().procedure(<name>).parameters(<parameters>).ifExists();
DB.drop().aggregate(<name>).parameters(<parameters>).ifExists();                                                 // #postgres
DB.drop().type(<name>).ifExists().cascade();                                                                     // #postgres
DB.drop().domain(<name>).ifExists().cascade();                                                                   // #postgres
DB.drop().role(<name>).ifExists();                                                                               // createRole#standard
DB.drop().user(<name>).ifExists();                                                                               // createRole#postgres
DB.drop().extension(<name>).ifExists().cascade();                                                                // #postgres
DB.drop().policy(<name>).on(<table>).ifExists();                                                                 // #postgres
DB.drop().rule(<name>).on(<table>).ifExists();                                                                   // #postgres

DB.rename().table(<name>).to(<name>);                                                                            // rename#standard
DB.reindex().table(<name>).concurrently();                                                                       // reindex#postgres
DB.reindex().index(<name>);                                                                                      // reindex#postgres
DB.reindex().schema(<name>);                                                                                     // reindex#postgres
```

## DCL
```
DB.grant(<privileges>).onTable(<tables>).to(<roles>).withGrantOption();                                          // grant#standard
DB.grant(<privileges>).onColumns(<table>, <columns>).to(<roles>);                                                // grant#standard
DB.grant(<privileges>).onSchema(<schema>).to(<roles>);                                                           // grant#standard
DB.grant(<privileges>).onDatabase(<database>).to(<roles>);                                                       // grant#standard
DB.grant(<privileges>).onSequence(<sequence>).to(<roles>);                                                       // grant#standard
DB.grant(<privileges>).onFunction(<name>, <parameters>).to(<roles>);                                             // grant#standard
DB.grant(<privileges>).onAllTablesInSchema(<schema>).to(<roles>);                                                // grant#standard
DB.grantRole(<roles>).to(<roles>).withAdminOption();                                                             // grantRole#standard
DB.revoke(<privileges>).onTable(<tables>).from(<roles>).cascade();                                               // grant#standard
DB.revokeGrantOption(<privileges>).onTable(<tables>).from(<roles>);                                              // grant#standard
DB.revokeRole(<roles>).from(<roles>);                                                                            // grantRole#standard
DB.alterDefaultPrivileges().inSchema(<schema>).forRole(<role>).grant(<privileges>).onTables().to(<roles>);        // #postgres
DB.setRole(<role>);                                                                                              // setRole#standard
DB.resetRole();                                                                                                  // setRole#standard
```

## Expression helpers
```
Cond.column(<column>).eq(<value>).and(Cond.column(<column>).like(<pattern>)).or(Cond.column(<column>).isNull());
Cond.column(<column>).between(<low>, <high>).not();
Cond.column(<column>).in(<values>);
Cond.column(<column>).isDistinctFrom(<value>);                                                                   // #standard
Cond.raw(<sql>, <parameters>);
Case.when(<condition>).then(<value>).when(<condition>).then(<value>).elseValue(<value>).end();
Case.on(<column>).when(<value>).then(<value>).elseValue(<value>).end();
Fn.coalesce(<values>);
Fn.cast(<expression>).as(<type>);
Fn.count().distinct(<column>).filter(<condition>);                                                               // filter#postgres
Col.of(<table>, <column>).as(<alias>);
```

## Merge groups
What each `<group>` tag collapses into, and what the dialect implementation has to do.

| Group | Merges into | Dialect work |
|---|---|---|
| `limit` | `limit(count).offset(count).withTies()` | `LIMIT/OFFSET` (PG, MySQL, SQLite), `FETCH FIRST … ROWS ONLY` (standard, Oracle 12c+, DB2), `TOP` (SQL Server), `ROWNUM` (legacy Oracle) |
| `setOp` | `union(query).all()`, `intersect(…)`, `except(…)` | Oracle spells `EXCEPT` as `MINUS`; `INTERSECT ALL` / `EXCEPT ALL` missing in several |
| `groupBy` | `groupBy(Group.rollup / cube / sets)` | MySQL only has `WITH ROLLUP`, in a different clause position |
| `join` | `join(source).type(JoinType).lateral().on(…) / .using(…)` | `LATERAL` (PG, MySQL 8, standard) vs `CROSS/OUTER APPLY` (SQL Server); MySQL has no `FULL JOIN`; SQL Server has no `USING` |
| `upsert` | `upsert(…).onConflict(…).doUpdate(…)` | `ON CONFLICT` (PG, SQLite), `ON DUPLICATE KEY UPDATE` (MySQL), `MERGE` (Oracle, SQL Server, DB2) |
| `merge` | keep as its own richer builder | source join, `whenNotMatchedBySource`, delete branches — strictly beyond upsert; PG needs 15+ |
| `returning` | `.returning(cols)` on insert/update/delete | `RETURNING` (PG, SQLite, MariaDB), `OUTPUT` (SQL Server), `RETURNING INTO` (Oracle), generated keys only (MySQL) |
| `correlatedDml` | `update(…).using(t)` / `delete(…).using(t)` | `UPDATE … FROM` (PG, SQL Server), join form (MySQL), `MERGE` (Oracle) |
| `set` | fold `setFrom` into `set` with a row-valued subquery | row-value assignment is PG/standard only |
| `lockMode` | `lockMode(Strength, WaitPolicy).of(tables)` | no `FOR UPDATE` in SQL Server — table hints `WITH (UPDLOCK, READPAST)`; MySQL < 8 uses `LOCK IN SHARE MODE` |
| `lockTable` | `lockTable(table, Mode, WaitPolicy)` — shares `WaitPolicy` with `lockMode` | PG `LOCK TABLE`, MySQL `LOCK TABLES`, no direct SQL Server form |
| `advisoryLock` | `advisoryLock(key).shared().tryOnly()` + `advisoryUnlock(key \| all)` | PG `pg_advisory_*`, MySQL `GET_LOCK`/`RELEASE_LOCK` (no shared mode), SQL Server `sp_getapplock` |
| `fetch` | `fetch(Direction, count).from(name)` | T-SQL `FETCH NEXT\|PRIOR\|ABSOLUTE n FROM`; `MOVE` is the same node with results discarded |
| `cursor` | one declare/close/`whereCurrentOf` builder | `BINARY`/`WITH HOLD`/`INSENSITIVE` are PG-specific modifiers |
| `serverPrepare` | `prepare / execute / deallocate` | PG and MySQL differ in parameter declaration; SQL Server uses `sp_prepare`; `deallocateAll` is PG-only |
| `twoPhase` | `prepareTransaction / commitPrepared / rollbackPrepared` | PG `PREPARE TRANSACTION`, MySQL `XA PREPARE/COMMIT/ROLLBACK` |
| `txn` | `begin().isolationLevel(…).readOnly()` | SQL Server `BEGIN TRANSACTION` + `SET TRANSACTION ISOLATION LEVEL`; `AND CHAIN` is standard/PG/MySQL 8; `DEFERRABLE` is PG-only |
| `savepoint` | savepoint / rollbackTo / release | SQL Server uses `SAVE TRANSACTION` and has no release |
| `frame` | `frame(FrameUnit, start, end, exclusion)` | `GROUPS` and `EXCLUDE` are PG 11+/standard; MySQL and SQLite support a subset |
| `filter` | `.filter(cond)` on any aggregate | native in PG/SQLite; mechanical rewrite to `CASE WHEN cond THEN expr END` elsewhere |
| `aggName` | dialect function registry, not distinct calls | `array_agg` (PG) / `string_agg` / `GROUP_CONCAT` (MySQL) / `STRING_AGG` (SQL Server) |
| `cteHint` | `.materialized(boolean)` | PG 12+ only; a no-op hint elsewhere |
| `cteSearch` | `.search(mode, cols).set(col)` / `.cycle(…)` | standard and PG 14+; unsupported elsewhere |
| `ctas` | one node for `SELECT … INTO` and `CREATE TABLE … AS` | `SELECT INTO` (SQL Server, PG legacy) vs `CTAS` (PG, Oracle, MySQL) |
| `bulk` | one load/unload builder | `COPY` (PG), `LOAD DATA INFILE` / `SELECT INTO OUTFILE` (MySQL), `BULK INSERT`/bcp (SQL Server) |
| `createTable` | `create().table(name).temporary().unlogged()` | MySQL renders unlogged as an engine choice; SQL Server renders temp via the `#name` prefix |
| `createView` | `create().view(name).orReplace().materialized().recursive()` | `OR REPLACE` (PG, Oracle), `CREATE OR ALTER` (SQL Server 2016+), drop-then-create (MySQL); matviews are PG/Oracle, SQL Server uses indexed views |
| `createIndex` | `create().index(name).unique().concurrently()` | `CONCURRENTLY` (PG), `ALGORITHM=INPLACE, LOCK=NONE` (MySQL), `WITH (ONLINE = ON)` (SQL Server) |
| `dropIndex` | same flags on drop | `DROP INDEX CONCURRENTLY` is PG-only; MySQL/SQL Server need the index's table |
| `createRole` | `create().role(name).login()` | a user *is* a role with `LOGIN` in PG; MySQL and SQL Server separate the two |
| `createRoutine` | `create().function(name).orReplace()` / `.procedure(name)` | headers merge; bodies do not (PL/pgSQL vs T-SQL vs PL/SQL) |
| `createTrigger` | `create().trigger(name).timing(…).forEach(…)` | `INSTEAD OF` needs a view; constraint triggers are PG-only |
| `createDatabase` | generic option bag | PG `ENCODING`/`OWNER` vs MySQL `CHARACTER SET`/`COLLATE` |
| `partition` | `partitionBy` / `partitionOf` / attach / detach | declarative partitioning is PG 10+; MySQL has its own inline syntax; SQL Server uses partition functions/schemes |
| `alterColumn` | `alterColumn(col).setType/​setDefault/…` | PG `ALTER COLUMN … USING`, MySQL `MODIFY COLUMN` (full redefinition), SQL Server `ALTER COLUMN` |
| `rename` | `rename(ObjectType, name).to(name)` | absorbs every `alter().<object>().renameTo()` **and** `DB.rename().table()`; MySQL `RENAME TABLE`, SQL Server `sp_rename` (a procedure, not DDL) |
| `reindex` | `reindex(target)` | PG `REINDEX`, MySQL `OPTIMIZE TABLE`, SQL Server `ALTER INDEX … REBUILD` |
| `truncateIdentity` | `truncate(t).restartIdentity()` | PG has the clause; MySQL always resets `AUTO_INCREMENT`; SQL Server needs a separate `DBCC CHECKIDENT` |
| `identityOverride` | `.overrideIdentity()` | PG `OVERRIDING SYSTEM VALUE`; SQL Server wraps in `SET IDENTITY_INSERT ON/OFF` |
| `defaultValues` | `.defaultValues()` | `DEFAULT VALUES` (PG, SQL Server) vs `VALUES ()` (MySQL) |
| `call` | `call(proc).with(args)` | `CALL` (standard, PG 11+, MySQL) vs `EXEC` (SQL Server) |
| `grant` | `grant(privs).on(ObjectType, names).to(roles)` | object-type dispatch more than dialect; `ALL TABLES IN SCHEMA` is PG-only |
| `grantRole` | `grantRole(roles).to(roles)` | separate statement in some dialects; SQL Server uses `ALTER ROLE … ADD MEMBER` |
| `setRole` | `setRole(role)` / `resetRole()` | SQL Server uses `EXECUTE AS` / `REVERT` |

Merging sections A/B above takes roughly 180 distinct call chains down to 110–120, with the
difference absorbed into enums (`JoinType`, `LockStrength`, `WaitPolicy`, `FetchDirection`,
`FrameUnit`, `GroupingMode`, `SetOperator`, `ObjectType`) and boolean modifiers.

This needs a dialect capability query (`supportsFilterClause()`, `supportsSkipLocked()`,
`supportsReturning()`, …) so a merged call can pick between native rendering, mechanical emulation
(`FILTER` → `CASE`, `MERGE` → upsert) and an explicit unsupported-feature failure. Without that
layer a merged call silently generates wrong SQL on the weaker dialect, which is worse than keeping
the calls separate. Open decision: whether emulation is automatic or opt-in — automatic
`FILTER` → `CASE` is harmless, but a `DISTINCT ON` → window-function rewrite changes the query plan
enough that an error may be preferable.

Not possible because we need a sinlge point where the query gets executed.
Above only possible in `truncate` and the other statements whose final chain element
already terminates the builder (`DB.commit()`, `DB.savepoint(<name>)`, `DB.checkpoint()`,
`DB.analyze(...)`, `DB.rename(...).to(...)`, `DB.comment().onTable(...).is(...)`).
With `join`, `where`, `orderBy`, window frames and CTE chaining included, the ambiguity
gets worse: almost every chain is a valid statement both with and without its tail, so a
terminal call (`.execute()`, `.fetch()`, `.build()`) is unavoidable — except for the
`DB.transaction().run(<block>)` form, where the block boundary is the execution point.
