package fr.insarennes.fafdti;

public class EntropyCriterion implements Criterion {

	@Override
	public boolean better(double value1, double value2) {
		return value1 < value2;
	}

	@Override
	public double compute(int[] distributionVector) {
		double criterionValue = 0;
		//calcul de la somme des ni
		float N = 0;
		for (int i = 0; i < distributionVector.length; i++)
			N += distributionVector[i];
		//calcul de l'entropie avec en base e
		for (int i = 0; i < distributionVector.length; i++) {
			float pi = distributionVector[i]/N;
			criterionValue += pi*Math.log(pi);
		}
		//pour tout mettre en base 2
		criterionValue /= -Math.log(2);
		return criterionValue;
	}

}
