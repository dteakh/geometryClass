import java.util.ArrayList;
import java.util.List;

class Geometry {

  record Point(double x, double y) {

    public Point add(Point other) {
      double newX = this.x() + other.x();
      double newY = this.y() + other.y();
      return new Point(newX, newY);
    }

    public Point subtract(Point other) {
      double newX = this.x() - other.x();
      double newY = this.y() - other.y();
      return new Point(newX, newY);
    }

    public Point multiply(double num) {
      double newX = this.x() * num;
      double newY = this.y() * num;
      return new Point(newX, newY);
    }

    public Point rotate(double angle) {
      double newX = this.x() * Math.cos(angle) - this.y() * Math.sin(angle);
      double newY = this.x() * Math.sin(angle) + this.y() * Math.cos(angle);
      return new Point(newX, newY);
    }

    public double length() {
      return Math.sqrt(this.x() * this.x() + this.y() * this.y());
    }

    public double distance(Point other) {
      return Math.sqrt(Math.pow(this.x() - other.x(), 2) + Math.pow(this.y() - other.y(), 2));
    }

    public Point normalize() {
      double newX = this.x() / this.length();
      double newY = this.y() / this.length();
      return new Point(newX, newY);
    }

  }

  abstract class Shape {

    abstract Point center();

    abstract double perimeter();

    abstract double area();

    abstract void translate(Point newCenter);

    abstract void rotate(double angle);

    abstract void scale(double coefficient);

  }

  class Ellipse extends Shape {

    Point firstFocus;
    Point secondFocus;
    double perifocus;

    public Ellipse(Point firstFocus, Point secondFocus, double perifocus) {
      this.firstFocus = firstFocus;
      this.secondFocus = secondFocus;
      this.perifocus = perifocus;
    }

    public List<Point> focuses() {
      List<Point> focuses = new ArrayList<>();
      focuses.add(this.firstFocus);
      focuses.add(this.secondFocus);
      return focuses;
    }

    public double focalDistance() {
      return firstFocus.distance(secondFocus) / 2;
    }

    public double majorSemiAxis() {
      return this.focalDistance() + this.perifocus;
    }

    public double minorSemiAxis() {
      double a = this.majorSemiAxis();
      double c = this.focalDistance();
      return Math.sqrt(a * a - c * c);
    }

    public double eccentricity() {
      double a = this.majorSemiAxis();
      double c = this.focalDistance();
      return c / a;
    }

    @Override
    public Point center() {
      Point sum = firstFocus.add(secondFocus);
      return new Point(sum.x() / 2, sum.y() / 2);
    }

    @Override
    public double perimeter() {
      double a = majorSemiAxis();
      double b = minorSemiAxis();
      return 4 * ((Math.PI * a * b + Math.pow(a - b, 2)) / (a + b));
    }

    @Override
    public double area() {
      return Math.PI * majorSemiAxis() * minorSemiAxis();
    }

    @Override
    public void translate(Point newCenter) {
      Point direction = newCenter.subtract(center());
      this.firstFocus = firstFocus.add(direction);
      this.secondFocus = secondFocus.add(direction);
    }

    @Override
    public void rotate(double angle) {
      Point center = center();
      Point rotatedVector = firstFocus.subtract(center()).rotate(angle);
      this.firstFocus = center.add(rotatedVector);
      this.secondFocus = center.subtract(rotatedVector);
    }

    @Override
    public void scale(double coefficient) {
      Point center = center();
      Point scaledVector = firstFocus.subtract(center()).multiply(coefficient);
      this.firstFocus = center.add(scaledVector);
      this.secondFocus = center.subtract(scaledVector);
      this.perifocus *= Math.abs(coefficient);
    }

  }

  class Circle extends Ellipse {

    public Circle(Point center, double radius) {
      super(center, center, radius);
    }

    public double radius() {
      return this.perifocus;
    }

  }

  class Rectangle extends Shape {

    Point firstPoint;
    Point secondPoint;
    double secondSide;

    public Rectangle(Point firstPoint, Point secondPoint, double secondSide) {
      this.firstPoint = firstPoint;
      this.secondPoint = secondPoint;
      this.secondSide = secondSide;
    }

    public List<Point> vertices() {
      double halfLength = this.secondSide / 2;
      Point firstSideVector = this.firstPoint.subtract(this.secondPoint).normalize();
      double rotatedX = -firstSideVector.y() * halfLength;
      double rotatedY = firstSideVector.x() * halfLength;
      Point secondSideHalfVector = new Point(rotatedX, rotatedY);

      List<Point> vertices = new ArrayList<>();
      vertices.add(this.firstPoint.add(secondSideHalfVector));
      vertices.add(this.secondPoint.add(secondSideHalfVector));
      vertices.add(this.secondPoint.subtract(secondSideHalfVector));
      vertices.add(this.firstPoint.subtract(secondSideHalfVector));
      return vertices;
    }

    public double firstSide() {
      return this.firstPoint.distance(this.secondPoint);
    }

    public double secondSide() {
      return this.secondSide;
    }

    public double diagonal() {
      return Math.sqrt(this.firstSide() * this.firstSide() + this.secondSide * this.secondSide);
    }

    @Override
    public Point center() {
      Point sum = new Point(0, 0);
      for (Point vertex : vertices()) {
        sum = sum.add(vertex);
      }
      return new Point(sum.x() / 4, sum.y() / 4);
    }

    @Override
    public double perimeter() {
      return 2 * (this.firstSide() + this.secondSide);
    }

    @Override
    public double area() {
      return this.firstSide() * this.secondSide;
    }

    @Override
    public void translate(Point newCenter) {
      Point translatedVector = newCenter.subtract(center());
      this.firstPoint = firstPoint.add(translatedVector);
      this.secondPoint = secondPoint.add(translatedVector);
    }

    @Override
    public void rotate(double angle) {
      Point center = center();
      Point rotatedVector = firstPoint.subtract(center).rotate(angle);
      this.firstPoint = center.add(rotatedVector);
      this.secondPoint = center.subtract(rotatedVector);
    }

    @Override
    public void scale(double coefficient) {
      Point center = center();
      Point scaledVector = firstPoint.subtract(center).multiply(coefficient);
      this.firstPoint = center.add(scaledVector);
      this.secondPoint = center.subtract(scaledVector);
      this.secondSide *= Math.abs(coefficient);
    }
  }

  class Square extends Rectangle {

    public Square(Point firstPoint, Point secondPoint) {
      super(firstPoint, secondPoint, firstPoint.distance(secondPoint));
    }

    public double side() {
      return this.secondSide;
    }

    public Circle circumscribedCircle() {
      return new Circle(center(), diagonal() / 2);
    }

    public Circle inscribedCircle() {
      return new Circle(center(), this.secondSide / 2);
    }

  }

  class Triangle extends Shape {

    Point firstPoint;
    Point secondPoint;
    Point thirdPoint;

    public Triangle(Point firstPoint, Point secondPoint, Point thirdPoint) {
      this.firstPoint = firstPoint;
      this.secondPoint = secondPoint;
      this.thirdPoint = thirdPoint;
    }

    public List<Point> vertices() {
      List<Point> vertices = new ArrayList<>();
      vertices.add(firstPoint);
      vertices.add(secondPoint);
      vertices.add(thirdPoint);
      return vertices;
    }

    public Circle circumscribedCircle() {
      double firstSide = firstPoint.distance(secondPoint);
      double secondSide = thirdPoint.distance(firstPoint);
      double thirdSide = secondPoint.distance(thirdPoint);

      double firstX =
          (Math.pow(firstPoint.x(), 2) - Math.pow(secondPoint.x(), 2) + Math.pow(firstPoint.y(), 2)
              - Math.pow(secondPoint.y(), 2)) * thirdPoint.y();
      double secondX =
          (Math.pow(thirdPoint.x(), 2) - Math.pow(firstPoint.x(), 2) + Math.pow(thirdPoint.y(), 2)
              - Math.pow(firstPoint.y(), 2)) * secondPoint.y();
      double thirdX =
          (Math.pow(secondPoint.x(), 2) - Math.pow(thirdPoint.x(), 2) + Math.pow(secondPoint.y(), 2)
              - Math.pow(thirdPoint.y(), 2)) * firstPoint.y();

      double firstY =
          (Math.pow(firstPoint.x(), 2) - Math.pow(secondPoint.x(), 2) + Math.pow(firstPoint.y(), 2)
              - Math.pow(secondPoint.y(), 2)) * thirdPoint.x();
      double secondY =
          (Math.pow(thirdPoint.x(), 2) - Math.pow(firstPoint.x(), 2) + Math.pow(thirdPoint.y(), 2)
              - Math.pow(firstPoint.y(), 2)) * secondPoint.x();
      double thirdY =
          (Math.pow(secondPoint.x(), 2) - Math.pow(thirdPoint.x(), 2) + Math.pow(secondPoint.y(), 2)
              - Math.pow(thirdPoint.y(), 2)) * firstPoint.x();

      double numX = -0.5 * (firstX + secondX + thirdX);
      double numY = 0.5 * (firstY + secondY + thirdY);
      double denominator =
          firstPoint.x() * (secondPoint.y() - thirdPoint.y()) + secondPoint.x() * (thirdPoint.y()
              - firstPoint.y()) + thirdPoint.x() * (firstPoint.y() - secondPoint.y());

      double centerX = numX / denominator;
      double centerY = numY / denominator;
      double radius = (firstSide * secondSide * thirdSide) / (4 * area());
      return new Circle(new Point(centerX, centerY), radius);
    }

    public Circle inscribedCircle() {
      double firstSide = firstPoint.distance(secondPoint);
      double secondSide = thirdPoint.distance(firstPoint);
      double thirdSide = secondPoint.distance(thirdPoint);

      System.out.println(thirdSide + " a " + firstPoint.x());
      double newX =
          (firstSide * thirdPoint.x() + secondSide * secondPoint.x() + thirdSide * firstPoint.x())
              / (
              firstSide + secondSide + thirdSide);
      double newY =
          (firstSide * thirdPoint.y() + secondSide * secondPoint.y() + thirdSide * firstPoint.y())
              / (
              firstSide + secondSide + thirdSide);
      double radius = 2 * area() / perimeter();
      return new Circle(new Point(newX, newY), radius);
    }

    public Point orthocenter() {
      double AX = thirdPoint.x() - secondPoint.x();
      double AY = thirdPoint.y() - secondPoint.y();
      double C1 = firstPoint.x() * AX + firstPoint.y() * AY;
      double BX = thirdPoint.x() - firstPoint.x();
      double BY = thirdPoint.y() - firstPoint.y();
      double C2 = secondPoint.x() * BX + secondPoint.y() * BY;

      double delta = AX * BY - BX * AY;
      double deltaX = C1 * BY - C2 * AY;
      double deltaY = C2 * AX - C1 * BX;
      double x = deltaX / delta;
      double y = deltaY / delta;

      return new Point(x, y);
    }

    public Circle ninePointsCircle() {
      Point circumscribedCenter = circumscribedCircle().center();
      Point orthocenter = orthocenter();
      double x = (circumscribedCenter.x() + orthocenter.x()) / 2;
      double y = (circumscribedCenter.y() + orthocenter.y()) / 2;
      double radius = circumscribedCircle().radius() / 2;
      return new Circle(new Point(x, y), radius);
    }

    @Override
    public Point center() {
      Point sum = firstPoint.add(secondPoint.add(thirdPoint));
      return new Point(sum.x() / 3, sum.y() / 3);
    }

    @Override
    public double perimeter() {
      return this.firstPoint.distance(this.secondPoint) + this.thirdPoint.distance(this.firstPoint)
          + this.secondPoint.distance(this.thirdPoint);
    }

    @Override
    public double area() {
      double firstSide = firstPoint.distance(secondPoint);
      double secondSide = thirdPoint.distance(firstPoint);
      double thirdSide = secondPoint.distance(thirdPoint);
      double p = perimeter() / 2;
      return Math.sqrt(p * (p - firstSide) * (p - secondSide) * (p - thirdSide));
    }

    @Override
    public void translate(Point newCenter) {
      Point translatedVector = newCenter.subtract(center());
      this.firstPoint = firstPoint.add(translatedVector);
      this.secondPoint = secondPoint.add(translatedVector);
      this.thirdPoint = thirdPoint.add(translatedVector);
    }

    @Override
    public void rotate(double angle) {
      Point center = center();
      Point firstRotated = firstPoint.subtract(center).rotate(angle);
      Point secondRotated = secondPoint.subtract(center).rotate(angle);
      Point thirdRotated = thirdPoint.subtract(center).rotate(angle);

      this.firstPoint = center.add(firstRotated);
      this.secondPoint = center.add(secondRotated);
      this.thirdPoint = center.add(thirdRotated);
    }

    @Override
    public void scale(double coefficient) {
      Point center = center();
      Point firstScaled = firstPoint.subtract(center).multiply(coefficient);
      Point secondScaled = secondPoint.subtract(center).multiply(coefficient);
      Point thirdScaled = thirdPoint.subtract(center).multiply(coefficient);

      this.firstPoint = center.add(firstScaled);
      this.secondPoint = center.add(secondScaled);
      this.thirdPoint = center.add(thirdScaled);
    }
  }
}