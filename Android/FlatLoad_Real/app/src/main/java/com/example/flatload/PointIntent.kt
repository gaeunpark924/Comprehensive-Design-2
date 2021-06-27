package com.example.flatload

import com.mapbox.geojson.Point
import java.io.Serializable

data class PointIntent(val point: Point): Serializable {
}