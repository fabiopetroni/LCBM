# LCBM
a fast and lightweight collaborative filtering algorithm for binary ratings.

If you use LCBM please cite the following paper:
-  F. Petroni, L. Querzoni, R. Beraldi, M. Paolucci:
   "LCBM: Statistics-Based Parallel Collaborative Filtering."
   In: Proceedings of the 17th International Conference on Business Information Systems (BIS), 2014.

###Hadoop MapReduce:

To run the project on hadoop type the following: 

```
bin/hadoop jar /home/hduser/LCBM_mapreduce.jar train test [options]
```

Parameters:
 - `train`: the name of the file with the train data
 - `test`: the name of the file with the test data.

Options:
 - `-k int`    ->      specifies the multiplicative factor for the SE. Default 2.
 - `-split_token char` ->  specifies the character that splits the dataset.
 - `-output1 string`   ->      specifies the name of the first output directory in the hdfs.
 - `-output2 sting`  ->      specifies the name of the second output directory in the hdfs.

### Example


```
bin/hadoop jar /home/hduser/LCBM_mapreduce.jar ml100k/trace1.base ml100k/trace1.test
```
