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
package io.github.mandar2812.dynaml.probability

import breeze.numerics.log
import breeze.stats.distributions.ContinuousDistr
import io.github.mandar2812.dynaml.analysis.PushforwardMap
import io.github.mandar2812.dynaml.pipes.DataPipe

/**
  * Created by mandar2812 on 24/09/2016.
  *
  * A measurable function is any mapping/function applied
  * to samples generated by some base random variable instance.
  *
  * @tparam Domain1 Type over which the base random variable is defined.
  * @tparam Domain2 Type over which output of function [[func]] takes its values.
  *
  * @param baseRV The base random variable
  * @param func The function to be applied cast as a [[DataPipe]] instance.
  */
class MeasurableFunction[
Domain1, Domain2](baseRV: RandomVariable[Domain1])(func: DataPipe[Domain1, Domain2])
  extends RandomVariable[Domain2] {

  override val sample: DataPipe[Unit, Domain2] = baseRV.sample > func

  def _baseRandomVar = baseRV

}

object MeasurableFunction {

  def apply[Domain1, @specialized Domain2](baseRV: RandomVariable[Domain1])(func: Domain1 => Domain2)
  : MeasurableFunction[Domain1, Domain2] = new MeasurableFunction(baseRV)(DataPipe(func))

}

class RealValuedMeasurableFunction[Domain1](baseRV: RandomVariable[Domain1])(func: DataPipe[Domain1, Double])
  extends MeasurableFunction[Domain1, Double](baseRV)(func) with ContinuousRandomVariable[Double]

object RealValuedMeasurableFunction {

  def apply[Domain1](baseRV: RandomVariable[Domain1])(func: (Domain1) => Double)
  : RealValuedMeasurableFunction[Domain1] = new RealValuedMeasurableFunction(baseRV)(DataPipe(func))
}


/**
  * A measurable function of a continuous random variable
  * with a defined probability density function.
  *
  * @tparam Domain1 The domain of the base random variable
  * @tparam Domain2 The output set of the function
  * @tparam Jacobian The type representing the Jacobian of inverse of the map [[p]]
  * @param baseRV The base random variable
  * @param p A function with a defined inverse and Jacobian of inverse
  *          as an [[PushforwardMap]] instance.
  *
  * */
class MeasurableDistrRV[Domain1, Domain2, Jacobian](
  baseRV: ContinuousDistrRV[Domain1])(p: PushforwardMap[Domain1, Domain2, Jacobian])
  extends MeasurableFunction[Domain1, Domain2](baseRV)(p)
    with RandomVarWithDistr[Domain2, ContinuousDistr[Domain2]] {

  override val underlyingDist = new ContinuousDistr[Domain2] {
    override def unnormalizedLogPdf(x: Domain2) =
      baseRV.underlyingDist.unnormalizedLogPdf(p.i(x)) + log(p._det(p.i.J(x)))

    override def logNormalizer = baseRV.underlyingDist.logNormalizer

    override def draw() = p.run(baseRV.underlyingDist.draw())
  }

}