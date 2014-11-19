package com.splicemachine.derby.impl.job.coprocessor;


import com.splicemachine.collections.SingletonSortedSet;

import java.io.IOException;
import java.util.SortedSet;

/**
 * @author Scott Fines
 *         Date: 4/15/14
 */
public class NoOpTaskSplitter implements TaskSplitter{
		public static final NoOpTaskSplitter INSTANCE = new NoOpTaskSplitter();

		@Override
		public SortedSet<SizedInterval> split(RegionTask task, byte[] taskStart, byte[] taskStop) throws IOException {
				return SingletonSortedSet.wrap(new SizedInterval(taskStart, taskStop, 0));
		}


}