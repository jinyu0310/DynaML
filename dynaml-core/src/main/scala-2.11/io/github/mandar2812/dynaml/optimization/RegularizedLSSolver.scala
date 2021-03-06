/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
* */

package io.github.mandar2812.dynaml.optimization

import breeze.linalg.{DenseMatrix, inv, DenseVector}

/**
  * Created by mandar on 9/2/16.
  */
class RegularizedLSSolver extends
  RegularizedOptimizer[DenseVector[Double],
    DenseVector[Double], Double,
    (DenseMatrix[Double], DenseVector[Double])] {
  /**
    * Solve the convex optimization problem.
    *
    * A = K<sup>T</sup>&middot;K + I*reg
    *
    * b = K<sup>T</sup>&middot;y
    *
    * return inverse(A)*b
    **/
  override def optimize(nPoints: Long,
                        ParamOutEdges: (DenseMatrix[Double], DenseVector[Double]),
                        initialP: DenseVector[Double]): DenseVector[Double] = {

    val (designMatrix,labels) = ParamOutEdges
    val smoother = DenseMatrix.eye[Double](initialP.length)*regParam
    //Construct matrix A and b block by block
    val A = designMatrix.t*designMatrix + smoother
    val b = designMatrix.t*labels

    inv(A)*b
  }
}
