# PostGIS Dialect Extension Guide

## Current State

Summary of current interfaces including inherited PostgreSQL methods.

## PostgisColumn Extensions

### Spatial Relationships (DE-9IM)

#### stEquals
```java
@NonNull SqlCondition stEquals(@NonNull T geometry);
```
Generates SQL: `ST_Equals(column, geometry)`

Returns true if two geometries are spatially equal (represent the same geometry, regardless of vertex order).

#### stDisjoint
```java
@NonNull SqlCondition stDisjoint(@NonNull T geometry);
```
Generates SQL: `ST_Disjoint(column, geometry)`

Returns true if the geometries do not share any space (no intersection at all).

#### stTouches
```java
@NonNull SqlCondition stTouches(@NonNull T geometry);
```
Generates SQL: `ST_Touches(column, geometry)`

Returns true if the geometries touch at their boundaries but their interiors do not intersect.

#### stCrosses
```java
@NonNull SqlCondition stCrosses(@NonNull T geometry);
```
Generates SQL: `ST_Crosses(column, geometry)`

Returns true if the geometries have some interior points in common but not all (e.g., a line crossing a polygon).

#### stOverlaps
```java
@NonNull SqlCondition stOverlaps(@NonNull T geometry);
```
Generates SQL: `ST_Overlaps(column, geometry)`

Returns true if the geometries share space but are not contained by each other (partial overlap of same dimension).

#### stRelate
```java
@NonNull SqlCondition stRelate(@NonNull T geometry, @NonNull String intersectionMatrix);
```
Generates SQL: `ST_Relate(column, geometry, 'intersectionMatrix')`

Tests the spatial relationship using a DE-9IM intersection matrix pattern (e.g., `'T*T***T**'`). This is the most general spatial relationship test.

#### stRelateMatrix
```java
@NonNull SqlExpression<String> stRelateMatrix(@NonNull T geometry);
```
Generates SQL: `ST_Relate(column, geometry)`

Returns the DE-9IM intersection matrix string describing the spatial relationship between two geometries.

#### stCovers
```java
@NonNull SqlCondition stCovers(@NonNull T geometry);
```
Generates SQL: `ST_Covers(column, geometry)`

Returns true if no point in the given geometry is outside this geometry. More inclusive than `ST_Contains`.

#### stCoveredBy
```java
@NonNull SqlCondition stCoveredBy(@NonNull T geometry);
```
Generates SQL: `ST_CoveredBy(column, geometry)`

Returns true if no point in this geometry is outside the given geometry.

#### stContainsProperly
```java
@NonNull SqlCondition stContainsProperly(@NonNull T geometry);
```
Generates SQL: `ST_ContainsProperly(column, geometry)`

Returns true if the given geometry is entirely within the interior (not touching the boundary).

### Measurement

#### stLength
```java
@NonNull SqlExpression<Double> stLength();
```
Generates SQL: `ST_Length(column)`

Returns the 2D length of a linestring or multi-linestring. For geographic coordinates, returns length in meters.

#### stLength3D
```java
@NonNull SqlExpression<Double> stLength3D();
```
Generates SQL: `ST_3DLength(column)`

Returns the 3D length of a linestring, taking Z coordinates into account.

#### stPerimeter
```java
@NonNull SqlExpression<Double> stPerimeter();
```
Generates SQL: `ST_Perimeter(column)`

Returns the perimeter of a polygon or multi-polygon.

#### stDistanceSphere
```java
@NonNull SqlExpression<Double> stDistanceSphere(@NonNull SqlColumn<?> other);
```
Generates SQL: `ST_DistanceSphere(column, other)`

Calculates the distance between two points on a sphere (Earth), in meters. Faster but less accurate than `ST_DistanceSpheroid`.

#### stDistanceSpheroid
```java
@NonNull SqlExpression<Double> stDistanceSpheroid(@NonNull SqlColumn<?> other);
```
Generates SQL: `ST_DistanceSpheroid(column, other)`

Calculates the distance between two points on a spheroid (WGS84 ellipsoid), in meters. More accurate than sphere.

#### stMaxDistance
```java
@NonNull SqlExpression<Double> stMaxDistance(@NonNull SqlColumn<?> other);
```
Generates SQL: `ST_MaxDistance(column, other)`

Returns the maximum distance between two geometries.

#### stAzimuth
```java
@NonNull SqlExpression<Double> stAzimuth(@NonNull SqlColumn<?> other);
```
Generates SQL: `ST_Azimuth(column, other)`

Returns the angle (in radians, clockwise from north) from this point to the other point.

#### stAngle
```java
@NonNull SqlExpression<Double> stAngle(@NonNull SqlColumn<?> p2, @NonNull SqlColumn<?> p3);
```
Generates SQL: `ST_Angle(column, p2, p3)`

Returns the angle between three points (with this column as vertex).

#### stHausdorffDistance
```java
@NonNull SqlExpression<Double> stHausdorffDistance(@NonNull SqlColumn<?> other);
```
Generates SQL: `ST_HausdorffDistance(column, other)`

Returns the Hausdorff distance between two geometries. A measure of shape similarity.

#### stFrechetDistance
```java
@NonNull SqlExpression<Double> stFrechetDistance(@NonNull SqlColumn<?> other);
```
Generates SQL: `ST_FrechetDistance(column, other)`

Returns the Frechet distance between two linestrings. Better than Hausdorff for comparing paths.

### Geometry Construction

#### stUnion
```java
@NonNull SqlExpression<?> stUnion(@NonNull SqlColumn<?> other);
```
Generates SQL: `ST_Union(column, other)`

Returns the geometric union of two geometries.

#### stIntersection
```java
@NonNull SqlExpression<?> stIntersection(@NonNull SqlColumn<?> other);
```
Generates SQL: `ST_Intersection(column, other)`

Returns the geometric intersection of two geometries.

#### stDifference
```java
@NonNull SqlExpression<?> stDifference(@NonNull SqlColumn<?> other);
```
Generates SQL: `ST_Difference(column, other)`

Returns the part of this geometry that does not intersect with the other.

#### stSymDifference
```java
@NonNull SqlExpression<?> stSymDifference(@NonNull SqlColumn<?> other);
```
Generates SQL: `ST_SymDifference(column, other)`

Returns the parts of both geometries that are not shared (symmetric difference).

#### stConvexHull
```java
@NonNull SqlExpression<?> stConvexHull();
```
Generates SQL: `ST_ConvexHull(column)`

Returns the convex hull (smallest convex polygon containing the geometry).

#### stConcaveHull
```java
@NonNull SqlExpression<?> stConcaveHull(double targetPercent);
```
Generates SQL: `ST_ConcaveHull(column, targetPercent)`

Returns a concave hull. The `targetPercent` (0-1) controls tightness: 1 = convex hull, lower = tighter fit.

#### stEnvelope
```java
@NonNull SqlExpression<?> stEnvelope();
```
Generates SQL: `ST_Envelope(column)`

Returns the bounding box of the geometry as a polygon.

#### stBoundary
```java
@NonNull SqlExpression<?> stBoundary();
```
Generates SQL: `ST_Boundary(column)`

Returns the boundary of the geometry (e.g., the ring of a polygon, the endpoints of a linestring).

#### stCollect
```java
@NonNull SqlExpression<?> stCollect();
```
Generates SQL: `ST_Collect(column)`

Aggregate function that collects geometries into a geometry collection.

#### stMakeLine
```java
@NonNull SqlExpression<?> stMakeLine();
```
Generates SQL: `ST_MakeLine(column)`

Aggregate function that creates a linestring from point geometries.

#### stMinimumBoundingCircle
```java
@NonNull SqlExpression<?> stMinimumBoundingCircle();
```
Generates SQL: `ST_MinimumBoundingCircle(column)`

Returns the smallest circle that contains the geometry.

#### stOffsetCurve
```java
@NonNull SqlExpression<?> stOffsetCurve(double distance);
```
Generates SQL: `ST_OffsetCurve(column, distance)`

Returns a line offset by the given distance from the input line. Positive = left, negative = right.

#### stVoronoiPolygons
```java
@NonNull SqlExpression<?> stVoronoiPolygons();
```
Generates SQL: `ST_VoronoiPolygons(column)`

Returns the Voronoi diagram of the input points.

#### stDelaunayTriangles
```java
@NonNull SqlExpression<?> stDelaunayTriangles();
```
Generates SQL: `ST_DelaunayTriangles(column)`

Returns a Delaunay triangulation of the input geometry.

### Geometry Processing

#### stSimplify
```java
@NonNull SqlExpression<?> stSimplify(double tolerance);
```
Generates SQL: `ST_Simplify(column, tolerance)`

Simplifies the geometry using the Douglas-Peucker algorithm. Reduces vertex count.

#### stSimplifyPreserveTopology
```java
@NonNull SqlExpression<?> stSimplifyPreserveTopology(double tolerance);
```
Generates SQL: `ST_SimplifyPreserveTopology(column, tolerance)`

Simplifies while guaranteeing that the result is topologically valid.

#### stSnap
```java
@NonNull SqlExpression<?> stSnap(@NonNull SqlColumn<?> other, double tolerance);
```
Generates SQL: `ST_Snap(column, other, tolerance)`

Snaps vertices of this geometry to the other geometry within the tolerance distance.

#### stMakeValid
```java
@NonNull SqlExpression<?> stMakeValid();
```
Generates SQL: `ST_MakeValid(column)`

Repairs an invalid geometry without losing vertices.

#### stReverse
```java
@NonNull SqlExpression<?> stReverse();
```
Generates SQL: `ST_Reverse(column)`

Reverses the vertex order of a geometry.

#### stForce2D
```java
@NonNull SqlExpression<?> stForce2D();
```
Generates SQL: `ST_Force2D(column)`

Forces the geometry to 2D by dropping Z and M coordinates.

#### stForce3D
```java
@NonNull SqlExpression<?> stForce3D();
```
Generates SQL: `ST_Force3D(column)`

Forces the geometry to 3D by adding Z coordinate (default 0).

#### stForce3DZ
```java
@NonNull SqlExpression<?> stForce3DZ(double z);
```
Generates SQL: `ST_Force3DZ(column)`

Forces geometry to 3D with an explicit default Z value.

#### stLineMerge
```java
@NonNull SqlExpression<?> stLineMerge();
```
Generates SQL: `ST_LineMerge(column)`

Merges a multilinestring into a connected linestring where possible.

#### stBuildArea
```java
@NonNull SqlExpression<?> stBuildArea();
```
Generates SQL: `ST_BuildArea(column)`

Creates a polygon from a set of lines that form a closed ring.

#### stSubstring
```java
@NonNull SqlExpression<?> stSubstring(double startFraction, double endFraction);
```
Generates SQL: `ST_LineSubstring(column, startFraction, endFraction)`

Returns a portion of a linestring between two fractional positions (0-1).

#### stInterpolatePoint
```java
@NonNull SqlExpression<Double> stInterpolatePoint(@NonNull T point);
```
Generates SQL: `ST_LineInterpolatePoint(column, fraction)` or `ST_LineLocatePoint(column, point)`

Returns the fractional position of the nearest point on the linestring to the given point.

### Geometry Accessors

#### stGeometryType
```java
@NonNull SqlExpression<String> stGeometryType();
```
Generates SQL: `ST_GeometryType(column)`

Returns the geometry type as a string (e.g., `'ST_Point'`, `'ST_Polygon'`, `'ST_LineString'`).

#### stSrid
```java
@NonNull SqlExpression<Integer> stSrid();
```
Generates SQL: `ST_SRID(column)`

Returns the spatial reference system identifier.

#### stSetSrid
```java
@NonNull SqlExpression<?> stSetSrid(int srid);
```
Generates SQL: `ST_SetSRID(column, srid)`

Sets the SRID without transforming coordinates. Use `stTransform` to actually reproject.

#### stNumPoints
```java
@NonNull SqlExpression<Integer> stNumPoints();
```
Generates SQL: `ST_NumPoints(column)`

Returns the number of points in a linestring.

#### stNPoints
```java
@NonNull SqlExpression<Integer> stNPoints();
```
Generates SQL: `ST_NPoints(column)`

Returns the total number of points in any geometry type.

#### stNumGeometries
```java
@NonNull SqlExpression<Integer> stNumGeometries();
```
Generates SQL: `ST_NumGeometries(column)`

Returns the number of geometries in a geometry collection.

#### stGeometryN
```java
@NonNull SqlExpression<?> stGeometryN(int n);
```
Generates SQL: `ST_GeometryN(column, n)`

Returns the Nth geometry from a geometry collection (1-based).

#### stPointN
```java
@NonNull SqlExpression<?> stPointN(int n);
```
Generates SQL: `ST_PointN(column, n)`

Returns the Nth point of a linestring (1-based).

#### stStartPoint
```java
@NonNull SqlExpression<?> stStartPoint();
```
Generates SQL: `ST_StartPoint(column)`

Returns the first point of a linestring.

#### stEndPoint
```java
@NonNull SqlExpression<?> stEndPoint();
```
Generates SQL: `ST_EndPoint(column)`

Returns the last point of a linestring.

#### stX
```java
@NonNull SqlExpression<Double> stX();
```
Generates SQL: `ST_X(column)`

Returns the X coordinate (longitude) of a point.

#### stY
```java
@NonNull SqlExpression<Double> stY();
```
Generates SQL: `ST_Y(column)`

Returns the Y coordinate (latitude) of a point.

#### stZ
```java
@NonNull SqlExpression<Double> stZ();
```
Generates SQL: `ST_Z(column)`

Returns the Z coordinate (elevation) of a point.

#### stM
```java
@NonNull SqlExpression<Double> stM();
```
Generates SQL: `ST_M(column)`

Returns the M coordinate (measure) of a point.

#### stNumInteriorRings
```java
@NonNull SqlExpression<Integer> stNumInteriorRings();
```
Generates SQL: `ST_NumInteriorRings(column)`

Returns the number of interior rings (holes) in a polygon.

#### stExteriorRing
```java
@NonNull SqlExpression<?> stExteriorRing();
```
Generates SQL: `ST_ExteriorRing(column)`

Returns the exterior ring of a polygon.

#### stInteriorRingN
```java
@NonNull SqlExpression<?> stInteriorRingN(int n);
```
Generates SQL: `ST_InteriorRingN(column, n)`

Returns the Nth interior ring of a polygon (1-based).

### Geometry Predicates

#### stIsValid
```java
@NonNull SqlCondition stIsValid();
```
Generates SQL: `ST_IsValid(column)`

Returns true if the geometry is valid according to OGC rules.

#### stIsValidReason
```java
@NonNull SqlExpression<String> stIsValidReason();
```
Generates SQL: `ST_IsValidReason(column)`

Returns a text description of why the geometry is invalid.

#### stIsSimple
```java
@NonNull SqlCondition stIsSimple();
```
Generates SQL: `ST_IsSimple(column)`

Returns true if the geometry has no self-intersections.

#### stIsEmpty
```java
@NonNull SqlCondition stIsEmpty();
```
Generates SQL: `ST_IsEmpty(column)`

Returns true if the geometry is empty.

#### stIsClosed
```java
@NonNull SqlCondition stIsClosed();
```
Generates SQL: `ST_IsClosed(column)`

Returns true if the linestring's start and end points are identical.

#### stIsRing
```java
@NonNull SqlCondition stIsRing();
```
Generates SQL: `ST_IsRing(column)`

Returns true if the linestring is closed and simple.

#### stIsCollection
```java
@NonNull SqlCondition stIsCollection();
```
Generates SQL: `ST_IsCollection(column)`

Returns true if the geometry is a collection type.

#### stDimension
```java
@NonNull SqlExpression<Integer> stDimension();
```
Generates SQL: `ST_Dimension(column)`

Returns the coordinate dimension (0 = point, 1 = line, 2 = surface).

#### stCoordDim
```java
@NonNull SqlExpression<Integer> stCoordDim();
```
Generates SQL: `ST_CoordDim(column)`

Returns the number of coordinate dimensions (2, 3, or 4).

### Output Formats

#### stAsText
```java
@NonNull SqlExpression<String> stAsText();
```
Generates SQL: `ST_AsText(column)`

Returns the WKT (Well-Known Text) representation.

#### stAsEwkt
```java
@NonNull SqlExpression<String> stAsEwkt();
```
Generates SQL: `ST_AsEWKT(column)`

Returns the Extended WKT representation (includes SRID).

#### stAsBinary
```java
@NonNull SqlExpression<?> stAsBinary();
```
Generates SQL: `ST_AsBinary(column)`

Returns the WKB (Well-Known Binary) representation.

#### stAsGeoJson
```java
@NonNull SqlExpression<String> stAsGeoJson();
```
Generates SQL: `ST_AsGeoJSON(column)`

Returns the GeoJSON representation.

#### stAsKml
```java
@NonNull SqlExpression<String> stAsKml();
```
Generates SQL: `ST_AsKML(column)`

Returns the KML representation.

#### stAsSvg
```java
@NonNull SqlExpression<String> stAsSvg();
```
Generates SQL: `ST_AsSVG(column)`

Returns the SVG path representation.

#### stAsMvt
```java
@NonNull SqlExpression<?> stAsMvt();
```
Generates SQL: `ST_AsMVT(row)`

Aggregate function that encodes geometries as Mapbox Vector Tiles.

#### stAsMvtGeom
```java
@NonNull SqlExpression<?> stAsMvtGeom(@NonNull T bounds, int extent);
```
Generates SQL: `ST_AsMVTGeom(column, bounds, extent)`

Transforms a geometry to the coordinate space of a Mapbox Vector Tile.

### Clustering

#### stClusterDBScan
```java
@NonNull SqlExpression<Integer> stClusterDBScan(double eps, int minPoints);
```
Generates SQL: `ST_ClusterDBSCAN(column, eps, minPoints) OVER ()`

Assigns a cluster ID using the DBSCAN algorithm. Window function.

#### stClusterKMeans
```java
@NonNull SqlExpression<Integer> stClusterKMeans(int numClusters);
```
Generates SQL: `ST_ClusterKMeans(column, numClusters) OVER ()`

Assigns a cluster ID using K-means clustering. Window function.

#### stClusterWithin
```java
@NonNull SqlExpression<?> stClusterWithin(double distance);
```
Generates SQL: `ST_ClusterWithin(column, distance)`

Aggregate function that groups geometries within the specified distance into clusters.

### Projection

#### stProject
```java
@NonNull SqlExpression<?> stProject(@NonNull T point, double distance, double azimuth);
```
Generates SQL: `ST_Project(point, distance, azimuth)`

Returns a point projected from the input point at the given distance and bearing (geographic coordinates).

#### geography
```java
@NonNull SqlExpression<?> geography();
```
Generates SQL: `column::geography`

Casts the geometry to geography type for accurate geodetic calculations.

#### geometry
```java
@NonNull SqlExpression<?> geometry();
```
Generates SQL: `column::geometry`

Casts from geography back to geometry type.

### Nearest Neighbor

#### orderByDistance
```java
@NonNull SqlColumn<T> orderByDistance(@NonNull T point);
```
Generates SQL: `ORDER BY column <-> point`

Uses the `<->` operator for index-assisted nearest-neighbor ordering. Much faster than `ORDER BY ST_Distance(...)` for finding the N closest geometries.

#### orderByDistanceCentroid
```java
@NonNull SqlColumn<T> orderByDistanceCentroid(@NonNull T point);
```
Generates SQL: `ORDER BY column <#> point`

Uses the `<#>` operator for bounding-box centroid distance ordering. Less accurate but faster.

---

## PostgisTable Extensions

### Spatial Index Types

#### createSpatialIndexND
```java
void createSpatialIndexND(@NonNull String name, @NonNull SqlColumn<?> geometryColumn);
```
Generates SQL: `CREATE INDEX name ON table USING GIST (geometryColumn gist_geometry_ops_nd)`

Creates a multi-dimensional (N-D) spatial index. Required for 3D spatial queries.

#### createSpgistSpatialIndex
```java
void createSpgistSpatialIndex(@NonNull String name, @NonNull SqlColumn<?> geometryColumn);
```
Generates SQL: `CREATE INDEX name ON table USING SPGIST (geometryColumn)`

Creates an SP-GiST spatial index. Can be faster than GiST for certain query patterns.

#### createBrinSpatialIndex
```java
void createBrinSpatialIndex(@NonNull String name, @NonNull SqlColumn<?> geometryColumn);
```
Generates SQL: `CREATE INDEX name ON table USING BRIN (geometryColumn)`

Creates a BRIN spatial index. Very compact, ideal for large datasets with spatially sorted data.

### Geometry Column Management

#### addGeometryColumn
```java
void addGeometryColumn(@NonNull String name, int srid, @NonNull String geometryType, int dimension);
```
Generates SQL: `SELECT AddGeometryColumn('table', 'name', srid, 'type', dimension)`

Adds a typed geometry column with SRID and dimension constraints.

#### setGeometryType
```java
void setGeometryType(@NonNull SqlColumn<?> column, @NonNull String geometryType, int srid);
```
Generates SQL: `ALTER TABLE ... ALTER COLUMN column TYPE geometry(geometryType, srid)`

Changes the type constraint on a geometry column.

### Clustering

#### clusterOnSpatialIndex
```java
void clusterOnSpatialIndex(@NonNull String indexName);
```
Generates SQL: `CLUSTER table USING indexName`

Physically reorders the table data according to the spatial index. Dramatically improves spatial query performance for range queries.

### Raster Support

#### addRasterColumn
```java
void addRasterColumn(@NonNull String name, int srid);
```
Generates SQL: `ALTER TABLE ... ADD COLUMN name raster` followed by `SELECT AddRasterConstraints('table', 'name', 'srid')`

Adds a raster column with SRID constraint.

#### addRasterConstraints
```java
void addRasterConstraints(@NonNull SqlColumn<?> column);
```
Generates SQL: `SELECT AddRasterConstraints('table', 'column')`

Adds all standard raster constraints (SRID, pixel type, alignment, etc.).

### Topology Support

#### addTopoGeometryColumn
```java
void addTopoGeometryColumn(@NonNull String topologyName, @NonNull String columnName, @NonNull String geometryType);
```
Generates SQL: `SELECT AddTopoGeometryColumn('topologyName', 'schema', 'table', 'columnName', 'geometryType')`

Adds a topology-aware geometry column.

---

## Supporting Types

### PostgisSpatialRefSys (utility)
Common SRID constants:
```java
public final class PostgisSrid {
    public static final int WGS84 = 4326;            // GPS coordinates (lat/lon)
    public static final int WEB_MERCATOR = 3857;      // Web maps (Google, OSM)
    public static final int UTM_32N = 32632;          // Central Europe
    public static final int NAD83 = 4269;             // North America
    public static final int ETRS89 = 4258;            // Europe
    public static final int UNKNOWN = 0;              // Unknown/unspecified
}
```

### PostgisGeometryType (new enum)
```java
public enum PostgisGeometryType {
    POINT,
    LINESTRING,
    POLYGON,
    MULTIPOINT,
    MULTILINESTRING,
    MULTIPOLYGON,
    GEOMETRYCOLLECTION,
    GEOMETRY
}
```
