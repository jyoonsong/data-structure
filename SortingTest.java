import java.io.*;
import java.util.*;

public class SortingTest
{
	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try
		{
			boolean isRandom = false;	// 입력받은 배열이 난수인가 아닌가?
			int[] value;	// 입력 받을 숫자들의 배열
			String nums = br.readLine();	// 첫 줄을 입력 받음
			if (nums.charAt(0) == 'r')
			{
				// 난수일 경우
				isRandom = true;	// 난수임을 표시

				String[] nums_arg = nums.split(" ");

				int numsize = Integer.parseInt(nums_arg[1]);	// 총 갯수
				int rminimum = Integer.parseInt(nums_arg[2]);	// 최소값
				int rmaximum = Integer.parseInt(nums_arg[3]);	// 최대값

				Random rand = new Random();	// 난수 인스턴스를 생성한다.

				value = new int[numsize];	// 배열을 생성한다.
				for (int i = 0; i < value.length; i++)	// 각각의 배열에 난수를 생성하여 대입
					value[i] = rand.nextInt(rmaximum - rminimum + 1) + rminimum;
			}
			else
			{
				// 난수가 아닐 경우
				int numsize = Integer.parseInt(nums);

				value = new int[numsize];	// 배열을 생성한다.
				for (int i = 0; i < value.length; i++)	// 한줄씩 입력받아 배열원소로 대입
					value[i] = Integer.parseInt(br.readLine());
			}

			// 숫자 입력을 다 받았으므로 정렬 방법을 받아 그에 맞는 정렬을 수행한다.
			while (true)
			{
				int[] newvalue = (int[])value.clone();	// 원래 값의 보호를 위해 복사본을 생성한다.

				String command = br.readLine();

				long t = System.currentTimeMillis();
				switch (command.charAt(0))
				{
					case 'B':	// Bubble Sort
						newvalue = DoBubbleSort(newvalue);
						break;
					case 'I':	// Insertion Sort
						newvalue = DoInsertionSort(newvalue);
						break;
					case 'H':	// Heap Sort
						newvalue = DoHeapSort(newvalue);
						break;
					case 'M':	// Merge Sort
						newvalue = DoMergeSort(newvalue);
						break;
					case 'Q':	// Quick Sort
						newvalue = DoQuickSort(newvalue);
						break;
					case 'R':	// Radix Sort
						newvalue = DoRadixSort(newvalue);
						break;
					case 'X':
						return;	// 프로그램을 종료한다.
					default:
						throw new IOException("잘못된 정렬 방법을 입력했습니다.");
				}
				if (isRandom)
				{
					// 난수일 경우 수행시간을 출력한다.
					System.out.println((System.currentTimeMillis() - t) + " ms");
				}
				else
				{
					// 난수가 아닐 경우 정렬된 결과값을 출력한다.
					for (int i = 0; i < newvalue.length; i++)
					{
						System.out.println(newvalue[i]);
					}
				}

			}
		}
		catch (IOException e)
		{
			System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
		}
	}

	private static int[] tmp; // for mergesort and radixSort

    ////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoBubbleSort(int[] value)
	{
	    // compare one by one
        for (int i = value.length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++)
                // move bigger item to right
                if (value[j] > value[j+1])
                    swap(value, j, j+1);
        }
		return (value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoInsertionSort(int[] value)
	{
        for (int i = 1; i < value.length; i++) {
            int newItem = value[i], loc;
            for (loc = i-1; loc >= 0 && newItem < value[loc]; loc--)
                value[loc+1] = value[loc];
            value[loc+1] = newItem;
        }
		return (value);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoHeapSort(int[] value)
	{
		int n = value.length;

		// Step 1: build initial heap A[1...n]
		for (int i = n/2; i >= 0; i--) // n/2는 최초로 heap 수선이 필요할 수 있는 부모
			percolateDown(value, i, n-1);

		// Step 2: delete one by one
		for (int size = n-1; size > 0; size--) {
			swap(value, 0, size);
			percolateDown(value, 0, size - 1);
		}

		return (value);
	}
	private static void percolateDown(int[] key, int i, int size) {
		int child = 2*i; // left child
		int right = 2*i + 1; // right child
		if (child <= size) {
			if ( right <= size && key[child] < key[right] )
				child = right; // index of larger child
			if ( key[i] < key[child] ) {
				swap(key, i, child);
				percolateDown(key, child, size);
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoMergeSort(int[] value)
	{
		tmp = new int[value.length];
		mergeSort(value, 0, value.length -1);
	    return (value);
	}

    private static void mergeSort(int[] value, int p, int r) {
        if (p < r) {
            int q = (p+r)/2;
            mergeSort(value, p, q);
            mergeSort(value, q+1, r);
            merge(value, p, q, r);
        }
    }

    private static void merge(int[] value, int p, int q, int r) {
	    int i = p, j = q+1, t = 0;
	    // merge value[p...q] and value[q+1...r]
	    while (i <= q && j <= r) {
	        if (value[i] <= value[j])
	            tmp[t++] = value[i++];
	        else tmp[t++] = value[j++];
        }
        // when value[p...q] remains not empty
        while (i <= q)
            tmp[t++] = value[i++];
	    // when value[q+1...r] remains not empty
        while (j <= r)
            tmp[t++] = value[j++];
        // save tmp to value
        i = p; t = 0;
        while (i <= r)
            value[i++] = tmp[t++];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoQuickSort(int[] value)
	{
        quickSort(value, 0, value.length -1);
		return (value);
	}

    private static void quickSort(int[] value, int p, int r) {
	    if (p < r) {
	        int q = partition(value, p, r);
	        quickSort(value, p, q-1);
	        quickSort(value, q+1, r);
        }
    }

    private static int partition(int[] value, int p, int r) {
	    // select last item as a pivot
	    int pivot = value[r];
	    // compare each element with pivot x and partition
	    int i = p-1;
	    for (int j = p; j <= r-1; j++)
	        if (value[j] <= pivot)
	            swap(value, ++i, j);
	    // set pivot in the right place
	    swap(value, i+1, r);
	    return (i+1);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int[] DoRadixSort(int[] value)
	{
	    // find the maximum number of digits
	    int maxDigits = 1;
	    int max = 0;
	    for (int i = 0; i < value.length; i++) {
	        int curr = value[i];
	        if (curr < 0)
	        	curr *= -1;
	        if (curr > max)
	            max = curr;
        }
        for (max /= 10; max > 0; max /= 10)
        	maxDigits *= 10;

        // do stable sort
		value = radixSort(value, maxDigits);

		return (value);
	}

    private static int[] radixSort(int[] value, int max) {
		int [] counter;
		tmp = new int[value.length];

        // sort from LSB to MSB
        for (int d = 1; d <= max; d *= 10) {
			// count elements per group
			counter = new int [19]; // -9 ~ 9

	        for (int i = 0; i < value.length; i++)
	            counter[ (value[i] / d) % 10 + 9 ]++;

	        for (int i = 1; i < counter.length; i++)
	        	counter[i] += counter[i-1];

	        for (int i = value.length - 1; i >= 0; i--)
	        	tmp[--counter[ (value[i] / d) % 10 + 9 ]] = value[i];

	        int[] original = value;
	        value = tmp;
	        tmp = original;
        }
        return value;
    }

    // swap method for bubbleSort & quickSort
    private static void swap(int[] value, int i, int j) {
        int tmp = value[i];
        value[i] = value[j];
        value[j] = tmp;
    }

}
