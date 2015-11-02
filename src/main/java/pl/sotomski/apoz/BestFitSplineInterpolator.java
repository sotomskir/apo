package pl.sotomski.apoz;

import javafx.animation.Interpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class BestFitSplineInterpolator extends Interpolator {

    final PolynomialSplineFunction f;

    public BestFitSplineInterpolator(double[] x, double[] y) {
        f = new SplineInterpolator().interpolate(x, y);
    }

    @Override protected double curve(double t) {
        return f.value(t);
    }
}