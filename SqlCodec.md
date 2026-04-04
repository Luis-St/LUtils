# Select

SqlDatabase#from(table) -> SqlQueryProvider#select(*) -> SqlSelectQuery gets executed

SqlQueryProvider#select() -> full entity selection
SqlQueryProvider#select(*columns) -> partial entity selection, only the specified columns/expressions are selected

SqlSelectQuery -> PreparedStatement -> ResultSet -> Type of the selected columns/expressions

## SqlSelectQuery -> PreparedStatement

for each selected column/expression:
- convert the column/expression to a SQL string
    - expressions wrap their inner columns/expressions into its SQL string
    - columns uses the SqlCodec to convert the column value to a SQL string

the selected columns/expressions are then combined into a SQL string with SELECT, FROM, JOIN, WHERE, GROUP BY, HAVING, ORDER BY, LIMIT, OFFSET clauses


## ResultSet -> Type of the selected columns/expressions

for each selected column/expression:
- get the 1-based index of the column/expression
- get the value of the column/expression from the ResultSet using the index
- map the value to the type of the column/expression using the SqlCodec

map of all values of the selected columns/expressions to the type of the selected columns/expressions is returned as a result of the query execution (entity, sql tuple, projection, etc.)
