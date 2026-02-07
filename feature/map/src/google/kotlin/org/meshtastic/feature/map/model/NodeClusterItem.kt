package org.meshtastic.feature.map.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import org.meshtastic.core.database.model.Node

data class NodeClusterItem(val node: Node, val nodePosition: LatLng, val nodeTitle: String, val nodeSnippet: String) :
    ClusterItem {
    override fun getPosition(): LatLng = nodePosition

    override fun getTitle(): String = nodeTitle

    override fun getSnippet(): String = nodeSnippet

    override fun getZIndex(): Float? = null

    fun getPrecisionMeters(): Double? {
        val precisionMap =
            mapOf(
                10 to 23345.484932,
                11 to 11672.7369,
                12 to 5836.36288,
                13 to 2918.175876,
                14 to 1459.0823719999053,
                15 to 729.53562,
                16 to 364.7622,
                17 to 182.375556,
                18 to 91.182212,
                19 to 45.58554,
            )
        return precisionMap[this.node.position.precisionBits]
    }
}
