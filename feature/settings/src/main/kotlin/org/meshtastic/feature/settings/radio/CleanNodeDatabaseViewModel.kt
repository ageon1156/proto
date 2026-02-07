package org.meshtastic.feature.settings.radio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.meshtastic.core.data.repository.NodeRepository
import org.meshtastic.core.database.entity.NodeEntity
import org.meshtastic.core.service.ServiceRepository
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

private const val MIN_DAYS_THRESHOLD = 7f


@HiltViewModel
class CleanNodeDatabaseViewModel
@Inject
constructor(
    private val nodeRepository: NodeRepository,
    private val serviceRepository: ServiceRepository,
) : ViewModel() {
    private val _olderThanDays = MutableStateFlow(30f)
    val olderThanDays = _olderThanDays.asStateFlow()

    private val _onlyUnknownNodes = MutableStateFlow(false)
    val onlyUnknownNodes = _onlyUnknownNodes.asStateFlow()

    private val _nodesToDelete = MutableStateFlow<List<NodeEntity>>(emptyList())
    val nodesToDelete = _nodesToDelete.asStateFlow()

    fun onOlderThanDaysChanged(value: Float) {
        _olderThanDays.value = value
    }

    fun onOnlyUnknownNodesChanged(value: Boolean) {
        _onlyUnknownNodes.value = value
        if (!value && _olderThanDays.value < MIN_DAYS_THRESHOLD) {
            _olderThanDays.value = MIN_DAYS_THRESHOLD
        }
    }

    
    fun getNodesToDelete() {
        viewModelScope.launch {
            val onlyUnknownEnabled = _onlyUnknownNodes.value
            val currentTimeSeconds = System.currentTimeMillis().milliseconds.inWholeSeconds
            val sevenDaysAgoSeconds = currentTimeSeconds - 7.days.inWholeSeconds
            val olderThanTimestamp = currentTimeSeconds - _olderThanDays.value.toInt().days.inWholeSeconds

            val initialNodesToConsider =
                if (onlyUnknownEnabled) {
                    
                    val olderNodes = nodeRepository.getNodesOlderThan(olderThanTimestamp.toInt())
                    val unknownNodes = nodeRepository.getUnknownNodes()
                    olderNodes.filter { itNode -> unknownNodes.any { unknownNode -> itNode.num == unknownNode.num } }
                } else {
                    
                    nodeRepository.getNodesOlderThan(olderThanTimestamp.toInt())
                }

            _nodesToDelete.value =
                initialNodesToConsider.filterNot { node ->
                    
                    (node.hasPKC && node.lastHeard >= sevenDaysAgoSeconds) ||
                        
                        node.isIgnored ||
                        node.isFavorite
                }
        }
    }

    
    fun cleanNodes() {
        viewModelScope.launch {
            val nodeNums = _nodesToDelete.value.map { it.num }
            if (nodeNums.isNotEmpty()) {
                nodeRepository.deleteNodes(nodeNums)

                val service = serviceRepository.meshService
                if (service != null) {
                    for (nodeNum in nodeNums) {
                        service.removeByNodenum(service.packetId, nodeNum)
                    }
                }
            }
            
            _nodesToDelete.value = emptyList()
        }
    }
}
