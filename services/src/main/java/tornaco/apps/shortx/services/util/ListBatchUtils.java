package tornaco.apps.shortx.services.util;

import java.util.ArrayList;
import java.util.List;

public class ListBatchUtils {

    public static class Batch<T> {
        public final List<List<T>> batches;

        public Batch(List<List<T>> batches) {
            this.batches = batches;
        }
    }

    public static <T> Batch<T> toArrayBatch(List<T> sources, int itemCountEachBatch) {
        if (sources == null || sources.isEmpty()) {
            return new Batch<>(new ArrayList<>(0));
        }
        List<List<T>> res = new ArrayList<>();
        int batchSize = sources.size() / itemCountEachBatch;
        for (int i = 0; i < batchSize; i++) {
            List<T> piece = sources.subList(0, itemCountEachBatch);
            res.add(new ArrayList<>(piece));
            sources.subList(0, itemCountEachBatch).clear();
        }
        if (!sources.isEmpty()) {
            res.add(new ArrayList<>(sources));
        }
        return new Batch<>(res);
    }
}
