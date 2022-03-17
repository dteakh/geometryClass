class Geometry {

    static record Point(double x, double y) {
    }

    static record Vector(double x, double y) {

        public double length() {
            return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        }

        public Vector multiply(double num) {
            return new Vector(x * num, y * num);
        }
    }

    abstract static class Shape {

        abstract Point center();

        abstract double perimeter();

        abstract double area();

        abstract void translate(Point newCenter);

        abstract void rotate(double angle);

        abstract void scale(double coefficient);

    }

    static class Ellipse extends Shape {

        Point firstFocus;
        Point secondFocus;
        double eccentricity;

        public Ellipse(Point firstFocus, Point secondFocus, double eccentricity) {
            this.firstFocus = firstFocus;
            this.secondFocus = secondFocus;
            this.eccentricity = eccentricity;
        }

        public Point firstFocus() {
            return firstFocus;
        }

        public Point secondFocus() {
            return secondFocus;
        }

        public double focalDistance() {
            return Math.sqrt(Math.pow(firstFocus.x - secondFocus.x, 2) + Math.pow(firstFocus.y - secondFocus.y, 2)) / 2;
        }

        public double majorSemiAxis() {
            return this.focalDistance() / eccentricity;
        }

        public double minorSemiAxis() {
            return Math.sqrt(majorSemiAxis() * majorSemiAxis() - focalDistance() * focalDistance());
        }

        public double eccentricity() {
            return eccentricity;
        }

        @Override
        public Point center() {
            return new Point((firstFocus.x + secondFocus.x) / 2, (firstFocus.y + secondFocus.y) / 2);
        }

        @Override
        public double perimeter() {
            double a = majorSemiAxis();
            double b = minorSemiAxis();
            return (4 * Math.PI * a * b + Math.pow(a - b, 2)) / (a + b);
        }

        @Override
        public double area() {
            return Math.PI * majorSemiAxis() * minorSemiAxis();
        }

        @Override
        public void translate(Point newCenter) {
            double translatedX = newCenter.x - this.center().x;
            double translatedY = newCenter.y - this.center().y;
            this.firstFocus = new Point(firstFocus.x + translatedX, firstFocus.y + translatedY);
            this.secondFocus = new Point(secondFocus.x + translatedX, secondFocus.y + translatedY);
        }

        @Override
        public void rotate(double angle) {
            Point center = center();
            Vector focalVector = new Vector(firstFocus.x - secondFocus.x, firstFocus.y - secondFocus.y);
            double rotatedX = focalVector.x * Math.cos(angle) - focalVector.y * Math.sin(angle);
            double rotatedY = focalVector.x * Math.cos(angle) + focalVector.y * Math.sin(angle);
            Vector rotatedVector = new Vector(rotatedX, rotatedY);
            this.firstFocus = new Point(center.x + rotatedVector.x, center.y + rotatedVector.y);
            this.secondFocus = new Point(center.x - rotatedVector.x, center.y - rotatedVector.y);
        }

        @Override
        public void scale(double coefficient) {
            Point center = center();
            Vector focalVector = new Vector(firstFocus.x - center().x, firstFocus.y - center().x);
            Vector scaledVector = focalVector.multiply(coefficient);
            this.firstFocus = new Point(scaledVector.x + center.x, scaledVector.y + center.y);
            this.secondFocus = new Point(center.x - scaledVector.x, center.y - scaledVector.y);
        }

    }

    static class Circle extends Ellipse {

        double radius;

        public Circle(Point center, double radius) {
            super(center, center, 0);
            this.radius = radius;
        }

        public double radius() {
            return radius;
        }

        @Override
        public void scale(double coefficient) {
            this.radius *= Math.abs(coefficient);
        }

    }

    static class Rectangle extends Shape {

        Point firstPoint;
        Point secondPoint;
        double firstSide;
        double secondSide;

        public Rectangle(Point firstPoint, Point secondPoint, double secondSide) {
            this.firstPoint = firstPoint;
            this.secondPoint = secondPoint;
            this.firstSide = Math.sqrt(Math.pow(firstPoint.x - secondPoint.x, 2) + Math.pow(firstPoint.y - secondPoint.y, 2));
            this.secondSide = secondSide;
        }

        public Point[] vertices() {
            Point[] vertices = new Point[4];
            Vector firstSideVector = new Vector(firstPoint.x - secondPoint.x, firstPoint.y - secondPoint.y);
            Vector unitVector = new Vector(firstSideVector.x / firstSideVector.length(), firstSideVector.y / firstSideVector.length());
            double rotatedX = -unitVector.y;
            double rotatedY = unitVector.x;
            Vector rotatedVector = new Vector(rotatedX, rotatedY);
            vertices[3] = new Point(firstPoint.x + rotatedVector.x * secondSide / 2, firstPoint.y + rotatedVector.y * secondSide / 2);
            vertices[2] = new Point(firstPoint.x - rotatedVector.x * secondSide / 2, firstPoint.y - rotatedVector.y * secondSide / 2);
            vertices[1] = new Point(secondPoint.x - rotatedVector.x * secondSide / 2, secondPoint.y - rotatedVector.y * secondSide / 2);
            vertices[0] = new Point(secondPoint.x + rotatedVector.x * secondSide / 2, secondPoint.y + rotatedVector.y * secondSide / 2);
            return vertices;
        }

        public double firstSide() {
            return firstSide;
        }

        public double secondSide() {
            return secondSide;
        }

        public double diagonal() {
            return Math.sqrt(firstSide * firstSide + secondSide * secondSide);
        }

        @Override
        public Point center() {
            double x = 0;
            double y = 0;
            for (Point vertex : vertices()) {
                x += vertex.x;
                y += vertex.y;
            }
            return new Point(x / 4, y / 4);
        }

        @Override
        public double perimeter() {
            return 2 * (firstSide + secondSide);
        }

        @Override
        public double area() {
            return firstSide * secondSide;
        }

        @Override
        public void translate(Point newCenter) {
            double translatedX = newCenter.x - this.center().x;
            double translatedY = newCenter.y - this.center().y;
            this.firstPoint = new Point(firstPoint.x + translatedX, firstPoint.y + translatedY);
            this.secondPoint = new Point(secondPoint.x + translatedX, secondPoint.y + translatedY);
        }

        @Override
        void rotate(double angle) {
            Point center = center();
            Vector halfSideVector = new Vector(firstPoint.x - center().x, firstPoint.y - center().y);
            double rotatedX = halfSideVector.x * Math.cos(angle) - halfSideVector.y * Math.sin(angle);
            double rotatedY = halfSideVector.x * Math.cos(angle) + halfSideVector.y * Math.sin(angle);
            Vector rotatedVector = new Vector(rotatedX, rotatedY);
            this.firstPoint = new Point(center.x + rotatedVector.x, center.y + rotatedVector.y);
            this.secondPoint = new Point(center.x - rotatedVector.x, center.y - rotatedVector.y);
        }

        @Override
        void scale(double coefficient) {
            Point center = center();
            Vector halfSideVector = new Vector(firstPoint.x - center.x, firstPoint.y - center.y);
            Vector scaledVector = halfSideVector.multiply(coefficient);
            this.firstPoint = new Point(center.x + scaledVector.x, center.y + scaledVector.y);
            this.secondPoint = new Point(center.x - scaledVector.x, center.y - scaledVector.y);
            this.firstSide = Math.sqrt(Math.pow(firstPoint.x - secondPoint.x, 2) + Math.pow(firstPoint.y - secondPoint.y, 2));
            this.secondSide *= Math.abs(coefficient);
        }
    }

    static class Square extends Rectangle {

        public Square(Point firstPoint, Point secondPoint) {
            super(firstPoint, secondPoint, new Vector(firstPoint.x - secondPoint.x, firstPoint.y - secondPoint.y).length());
        }

        public double side() {
            return firstSide;
        }

        public Circle circumscribedCircle() {
            return new Circle(center(), diagonal() / 2);
        }

        public Circle inscribedCircle() {
            return new Circle(center(), firstSide / 2);
        }

    }

    static class Triangle extends Shape {

        Point firstPoint;
        Point secondPoint;
        Point thirdPoint;
        double firstSide;
        double secondSide;
        double thirdSide;

        public Triangle(Point firstPoint, Point secondPoint, Point thirdPoint) {
            this.firstPoint = firstPoint;
            this.secondPoint = secondPoint;
            this.thirdPoint = thirdPoint;
            this.firstSide = new Vector(firstPoint.x - secondPoint.x, firstPoint.y - secondPoint.y).length();
            this.secondSide = new Vector(thirdPoint.x - firstPoint.x, thirdPoint.y - firstPoint.y).length();
            this.thirdSide = new Vector(secondPoint.x - thirdPoint.x, secondPoint.y - thirdPoint.y).length();
        }

        public Point firstPoint() {
            return this.firstPoint;
        }

        public Point secondPoint() {
            return this.secondPoint;
        }

        public Point thirdPoint() {
            return this.thirdPoint;
        }

        public Circle circumscribedCircle() {
            double firstX = (Math.pow(firstPoint.x, 2) - Math.pow(secondPoint.x, 2) + Math.pow(firstPoint.y, 2) - Math.pow(secondPoint.y, 2)) * thirdPoint.y;
            double secondX = (Math.pow(thirdPoint.x, 2) - Math.pow(firstPoint.x, 2) + Math.pow(thirdPoint.y, 2) - Math.pow(firstPoint.y, 2)) * secondPoint.y;
            double thirdX = (Math.pow(secondPoint.x, 2) - Math.pow(thirdPoint.x, 2) + Math.pow(secondPoint.y, 2) - Math.pow(thirdPoint.y, 2)) * firstPoint.y;

            double firstY = (Math.pow(firstPoint.x, 2) - Math.pow(secondPoint.x, 2) + Math.pow(firstPoint.y, 2) - Math.pow(secondPoint.y, 2)) * thirdPoint.x;
            double secondY = (Math.pow(thirdPoint.x, 2) - Math.pow(firstPoint.x, 2) + Math.pow(thirdPoint.y, 2) - Math.pow(firstPoint.y, 2)) * secondPoint.x;
            double thirdY = (Math.pow(secondPoint.x, 2) - Math.pow(thirdPoint.x, 2) + Math.pow(secondPoint.y, 2) - Math.pow(thirdPoint.y, 2)) * firstPoint.x;

            double numX = -0.5 * (firstX + secondX + thirdX);
            double numY = 0.5 * (firstY + secondY + thirdY);
            double denominator = firstPoint.x * (secondPoint.y - thirdPoint.y) + secondPoint.x * (thirdPoint.y - firstPoint.y) + thirdPoint.x * (firstPoint.y - secondPoint.y);

            double centerX = numX / denominator;
            double centerY = numY / denominator;
            double radius = (firstSide * secondSide * thirdSide) / (4 * area());
            return new Circle(new Point(centerX, centerY), radius);
        }

        public Circle inscribedCircle() {
            double newX = (firstSide * firstPoint.x + secondSide * secondPoint.x + thirdSide * thirdPoint.x) / (firstSide + secondSide + thirdSide);
            double newY = (firstSide * firstPoint.y + secondSide * secondPoint.y + thirdSide * thirdPoint.y) / (firstSide + secondSide + thirdSide);
            double radius = 2 * area() / perimeter();
            return new Circle(new Point(newX, newY), radius);
        }

        public Point orthocenter() {
            double AX = thirdPoint.x - secondPoint.x;
            double AY = thirdPoint.y - secondPoint.y;
            double C1 = firstPoint.x * AX + firstPoint.y * AY;
            double BX = thirdPoint.x - firstPoint.x;
            double BY = thirdPoint.y - firstPoint.y;
            double C2 = secondPoint.x * BX + secondPoint.y * BY;

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
            double x = (circumscribedCenter.x + orthocenter.x) / 2;
            double y = (circumscribedCenter.y + orthocenter.y) / 2;
            double radius = circumscribedCircle().radius() / 2;
            return new Circle(new Point(x, y), radius);
        }

        @Override
        public Point center() {
            double x = firstPoint.x + secondPoint.x + thirdPoint.x;
            double y = firstPoint.y + secondPoint.y + thirdPoint.y;
            return new Point(x / 3, y / 3);
        }

        @Override
        public double perimeter() {
            return firstSide + secondSide + thirdSide;
        }

        @Override
        public double area() {
            double p = perimeter() / 2;
            return Math.sqrt(p * (p - firstSide) * (p - secondSide) * (p - thirdSide));
        }

        @Override
        public void translate(Point newCenter) {
            double translatedX = newCenter.x - this.center().x;
            double translatedY = newCenter.y - this.center().y;
            this.firstPoint = new Point(firstPoint.x + translatedX, firstPoint.y + translatedY);
            this.secondPoint = new Point(secondPoint.x + translatedX, secondPoint.y + translatedY);
            this.thirdPoint = new Point(thirdPoint.x + translatedX, thirdPoint.y + translatedY);
        }

        @Override
        public void rotate(double angle) {
            Point center = center();
            Vector firstPointVector = new Vector(firstPoint.x - center.x, firstPoint.y - center.y);
            Vector secondPointVector = new Vector(secondPoint.x - center.x, secondPoint.y - center.y);
            Vector thirdPointVector = new Vector(thirdPoint.x - center.x, thirdPoint.y - center.y);

            double rotatedX = firstPointVector.x * Math.cos(angle) - firstPointVector.y * Math.sin(angle);
            double rotatedY = firstPointVector.x * Math.cos(angle) + firstPointVector.y * Math.sin(angle);
            Vector rotatedFirst = new Vector(rotatedX, rotatedY);

            rotatedX = secondPointVector.x * Math.cos(angle) - secondPointVector.y * Math.sin(angle);
            rotatedY = secondPointVector.x * Math.cos(angle) + secondPointVector.y * Math.sin(angle);
            Vector rotatedSecond = new Vector(rotatedX, rotatedY);

            rotatedX = thirdPointVector.x * Math.cos(angle) - thirdPointVector.y * Math.sin(angle);
            rotatedY = thirdPointVector.x * Math.cos(angle) + thirdPointVector.y * Math.sin(angle);
            Vector rotatedThird = new Vector(rotatedX, rotatedY);

            this.firstPoint = new Point(center.x + rotatedFirst.x, center.y + rotatedFirst.y);
            this.secondPoint = new Point(center.x + rotatedSecond.x, center.y + rotatedSecond.y);
            this.thirdPoint = new Point(center.x + rotatedThird.x, center.y + rotatedThird.y);
        }

        @Override
        public void scale(double coefficient) {
            Point center = center();
            Vector firstPointVector = new Vector(firstPoint.x - center.x, firstPoint.y - center.y);
            Vector secondPointVector = new Vector(secondPoint.x - center.x, secondPoint.y - center.y);
            Vector thirdPointVector = new Vector(thirdPoint.x - center.x, thirdPoint.y - center.y);

            Vector scaledFirst = firstPointVector.multiply(coefficient);
            Vector scaledSecond = secondPointVector.multiply(coefficient);
            Vector scaledThird = thirdPointVector.multiply(coefficient);

            this.firstPoint = new Point(center.x + scaledFirst.x, center.y + scaledFirst.y);
            this.secondPoint = new Point(center.x + scaledSecond.x, center.y + scaledSecond.y);
            this.thirdPoint = new Point(center.x + scaledThird.x, center.y + scaledThird.y);
        }
    }
}
