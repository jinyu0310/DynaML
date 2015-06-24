package org.kuleuven.esat.kernels

import breeze.linalg.{norm, DenseMatrix, DenseVector}

/**
 * Standard RBF Kernel of the form
 * K(Xi,Xj) = exp(-||Xi - Xj||**2/2*bandwidth**2)
 */

class RBFKernel(private var bandwidth: Double)
  extends SVMKernel[DenseMatrix[Double]]
  with Serializable {

  override val hyper_parameters = List("bandwidth")

  def setbandwidth(d: Double): Unit = {
    this.bandwidth = d
  }

  override def evaluate(x: DenseVector[Double], y: DenseVector[Double]): Double = {
    val diff = x - y
    Math.exp(-1*math.pow(norm(diff, 2), 2)/(2*math.pow(bandwidth, 2)))
  }

  override def buildKernelMatrix(
      mappedData: List[DenseVector[Double]],
      length: Int): KernelMatrix[DenseMatrix[Double]] =
    SVMKernel.buildSVMKernelMatrix(mappedData, length, this.evaluate)

  def getBandwidth: Double = this.bandwidth

}
