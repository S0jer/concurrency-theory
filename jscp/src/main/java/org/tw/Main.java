package org.tw;

import org.jcsp.lang.*;

import java.util.ArrayList;
import java.util.List;

public final class Main {
    static int numberOfProducers = 30;
    static int numberOfConsumers = 30;
    static int numberOfBuffers = 6;
    static int bufferSize = 6;
    static int maxClientAction = bufferSize / 2;

    public static void main(String[] args) {
        List<CSProcess> processList = new ArrayList<>();

        addProducers(processList);
        addConsumers(processList);
        handleBuffers(processList);
        run(processList);
    }

    private static void run(List<CSProcess> processList) {
        CSProcess[] convertedProcessList = new CSProcess[processList.size()];
        convertedProcessList = processList.toArray(convertedProcessList);

        Parallel par = new Parallel(convertedProcessList);
        par.run();
    }

    private static void addConsumers(List<CSProcess> processList) {
        for (int i = 0; i < numberOfConsumers; i++) {
            One2OneChannel channel1 = Channel.one2one();
            One2OneChannel channel2 = Channel.one2one();
            DualChannel dualChannel = new DualChannel(channel1, channel2);
            processList.add(new Consumer(dualChannel, maxClientAction, i));
        }
    }

    private static void addProducers(List<CSProcess> processList) {
        for (int i = 0; i < numberOfProducers; i++) {
            One2OneChannel channel1 = Channel.one2one();
            One2OneChannel channel2 = Channel.one2one();
            DualChannel dualChannel = new DualChannel(channel1, channel2);
            processList.add(new Producer(dualChannel, maxClientAction, i));
        }
    }

    private static void handleBuffers(List<CSProcess> processList) {
        List<Integer> clientBuffer = new ArrayList<>();
        int bufferIndex = 0;
        addClientBufferIndexes(processList, bufferIndex, clientBuffer);

        int firstBufferIndex = numberOfProducers + numberOfConsumers;

        for (int i = 0; i < numberOfBuffers; i++) {
            List<DualChannel> clientChannels = new ArrayList<>();
            for (int j = 0; j < clientBuffer.size(); j++) {
                if (clientBuffer.get(j) == i) {
                    var client = processList.get(j);
                    if (client instanceof Producer) {
                        clientChannels.add(((Producer) processList.get(j)).getBufferGate());
                    } else if (client instanceof Consumer) {
                        clientChannels.add(((Consumer) processList.get(j)).getBufferGate());
                    }
                }
            }
            One2OneChannel forwardChannel = null;
            One2OneChannel backwardChannel = null;

            if (i == 0) {
                forwardChannel = Channel.one2one();
                backwardChannel = Channel.one2one();
            } else if (i < numberOfBuffers - 1) {
                forwardChannel = Channel.one2one();
                backwardChannel = ((Buffer) processList.get(firstBufferIndex + i - 1)).getForwardBuffer();
            } else if (i == numberOfBuffers - 1) {
                forwardChannel = ((Buffer) processList.get(firstBufferIndex)).getBackwardBuffer();
                backwardChannel = ((Buffer) processList.get(firstBufferIndex + i - 1)).getForwardBuffer();
            }
            processList.add(new Buffer(forwardChannel, backwardChannel, clientChannels, bufferSize, i));
        }
    }

    private static void addClientBufferIndexes(List<CSProcess> processList, int bufferIndex, List<Integer> clientBuffer) {
        for (int i = 0; i < processList.size(); i++) {
            System.out.println(bufferIndex);
            clientBuffer.add(bufferIndex);
            bufferIndex = (bufferIndex + 1) % numberOfBuffers;
        }
    }
}
