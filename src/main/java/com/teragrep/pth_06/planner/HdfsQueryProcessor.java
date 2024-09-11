package com.teragrep.pth_06.planner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.teragrep.pth_06.config.Config;
import com.teragrep.pth_06.HdfsTopicPartitionOffsetMetadata;
import com.teragrep.pth_06.planner.offset.HdfsOffset;
import com.teragrep.pth_06.planner.walker.HdfsConditionWalker;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

// Class for processing hdfs queries.
public class HdfsQueryProcessor implements HdfsQuery {
    private final Logger LOGGER = LoggerFactory.getLogger(HdfsQueryProcessor.class);
    private LinkedList<HdfsTopicPartitionOffsetMetadata> topicPartitionList;
    private final HdfsDBClient hdfsDBClient;
    private String topicsRegexString;
    private Map<TopicPartition, Long> hdfsOffsetMap;

    public HdfsQueryProcessor(Config config) {
        // Filter only topics using regex pattern
        topicsRegexString = null;
        if (config.query != null) {
            try {
                HdfsConditionWalker parser = new HdfsConditionWalker();
                topicsRegexString = parser.fromString(config.query);
            } catch (Exception e) {
                throw new RuntimeException("HdfsQueryProcessor problems when construction Query conditions query:" + config.query + " exception:" + e);
            }
        }
        if (topicsRegexString == null) {
            topicsRegexString = "^.*$"; // all topics if none given
        }
        // Implement hdfs db client that fetches the metadata for the files that are stored in hdfs based on topic name (aka. directory containing the files for a specific topic in HDFS).
        // Remember to implement Kerberized HDFS access for prod. Tests are done using normal access on mini cluster.
        try {
            this.hdfsDBClient = new HdfsDBClient(
                    config,
                    topicsRegexString // topicsRegexString only searches for the given topic/topics (aka. directories).
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Generate the new starting partition offsets for kafka.
        hdfsOffsetMap = new HashMap<>(); // This parameter is used for generating the new start offsets for the KafkaOffsetPlanner. hdfsOffsetMapToJSON() is used to transfer the parameter using printer.
        seekToResults(); // This method loads all the available metadata to TopicPartitionList from HDFS.
        // Create a map that only contains the metadata with the highest offset for every topic partition.
        for (HdfsTopicPartitionOffsetMetadata r : topicPartitionList) {
            long partitionStart = r.endOffset;
            if (!hdfsOffsetMap.containsKey(r.topicPartition)) {
                hdfsOffsetMap.put(r.topicPartition, partitionStart+1);
            } else {
                if (hdfsOffsetMap.get(r.topicPartition) < partitionStart) {
                    hdfsOffsetMap.replace(r.topicPartition, partitionStart+1);
                }
            }
        }
        LOGGER.debug("HdfsQueryProcessor.HdfsQueryProcessor>");
    }

    // pulls the metadata for available topic partition files in HDFS.
    private void seekToResults() {
        LOGGER.debug("HdfsQueryProcessor.seekToResults>");
        try {
            topicPartitionList = hdfsDBClient.pullToPartitionList(); // queries the list of topic partitions based on the topic name condition filtering.
            LOGGER.debug("HdfsQueryProcessor.seekToResults>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Returns all the available HDFS file metadata between the given topic partition offsets.
    @Override
    public LinkedList<HdfsTopicPartitionOffsetMetadata> processBetweenHdfsFileMetadata(HdfsOffset startOffset, HdfsOffset endOffset) {
        LinkedList<HdfsTopicPartitionOffsetMetadata> rv = new LinkedList<>();
        Map<TopicPartition, Long> endOffsetMap = endOffset.getOffsetMap();
        Map<TopicPartition, Long> startOffsetMap = startOffset.getOffsetMap();
        for (HdfsTopicPartitionOffsetMetadata r : topicPartitionList) {
            if ( (endOffsetMap.get(r.topicPartition) >= r.endOffset) & (startOffsetMap.get(r.topicPartition) <= r.endOffset) ) {
                rv.add(new HdfsTopicPartitionOffsetMetadata(r.topicPartition, r.endOffset, r.hdfsFilePath, r.hdfsFileSize));
            }
        }
        return rv;
    }

    // Removes the committed topic partition offsets from topicPartitionList.
    @Override
    public void commit(HdfsOffset offset) {
        Map<TopicPartition, Long> offsetMap = offset.getOffsetMap();
        LinkedList<HdfsTopicPartitionOffsetMetadata> newTopicPartitionList = new LinkedList<>();
        // Generate new topicPartitionList where the metadata with offset values lower than the offset values given as input parameter are filtered out.
        for (HdfsTopicPartitionOffsetMetadata r : topicPartitionList) {
            if (offsetMap.get(r.topicPartition) < r.endOffset) {
                newTopicPartitionList.add(new HdfsTopicPartitionOffsetMetadata(r.topicPartition, r.endOffset, r.hdfsFilePath, r.hdfsFileSize));
            }
        }
        topicPartitionList = newTopicPartitionList;
    }

    // Prints json array containing the starting offsets for kafka to use.
    @Override
    public JsonArray hdfsOffsetMapToJSON() {
        JsonArray ja = new JsonArray();
        for (Map.Entry<TopicPartition, Long> entry : hdfsOffsetMap.entrySet()) {
            String topic = entry.getKey().topic();
            String partition = String.valueOf(entry.getKey().partition());
            String offset = String.valueOf(entry.getValue());
            JsonObject jo = new JsonObject();
            jo.addProperty("topic", topic);
            jo.addProperty("partition", partition);
            jo.addProperty("offset", offset);
            ja.add(jo);
        }
        return ja;
    }

    // returns the starting offsets for all available (aka. filtered) topic partitions.
    @Override
    public HdfsOffset getBeginningOffsets() {
        Map<TopicPartition, Long> startOffset = new HashMap<>();
        // Go through the topicPartitionList to generate start offsets.
        for (HdfsTopicPartitionOffsetMetadata r : topicPartitionList) {
            // Generate startOffset
            // When going through the result, store the topic partition with the lowest offset to the startOffset object.
            long partitionOffset = r.endOffset;
            if (!startOffset.containsKey(r.topicPartition)) {
                startOffset.put(r.topicPartition, partitionOffset);
            } else {
                if (startOffset.get(r.topicPartition) > partitionOffset) {
                    startOffset.replace(r.topicPartition, partitionOffset);
                }
            }
        }
        return new HdfsOffset(startOffset);
    }

    // returns the end offsets for all available (aka. filtered) topic partitions.
    @Override
    public HdfsOffset getInitialEndOffsets() {
        Map<TopicPartition, Long> endOffset = new HashMap<>();
        // Go through the topicPartitionList to generate end offsets.
        for (HdfsTopicPartitionOffsetMetadata r : topicPartitionList) {
            long partitionOffset = r.endOffset;
            // Generate endOffset
            // When going through the result, store the topic partition with the highest offset to the endOffset object.
            if (!endOffset.containsKey(r.topicPartition)) {
                endOffset.put(r.topicPartition, partitionOffset);
            } else {
                if (endOffset.get(r.topicPartition) < partitionOffset) {
                    endOffset.replace(r.topicPartition, partitionOffset);
                }
            }
        }
        return new HdfsOffset(endOffset);
    }
}