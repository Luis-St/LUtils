call -> guard -> create -> filter -> transform -> gate -> [async boundary] -> router -> format -> append

if async:
caller thread: guard -> create(capture) -> filter -> transform -> enqueue
consumer thread: dequeue -> sink-filter -> [sink-transform] -> format -> append

1. call: Call on the Logger
2. guard: Check if the log level is enabled
3. create: Create a LogEvent object with the provided message and metadata
4. filter: Apply any filters to determine if the event should be logged
5. transform: Transform the LogEvent object if necessary (e.g., adding additional context)
6. gate: Filter the LogEvent again after transformation to ensure it still meets logging criteria
7. async boundary: If logging asynchronously, enqueue the LogEvent for processing in a separate thread
8. router: Route the LogEvent to the appropriate output destination (e.g., console, file, remote server) (if async, this happens in the consumer thread) (different log levels may have different routing rules)
9. format: Format the LogEvent into a string representation suitable for output
10. append: Append the formatted log message to the appropriate output (e.g., console, file, etc.)
