package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import serveur.element.Caracteristique;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;

public class DeplacementRapide {
	/**
	 * Vue du personnage qui veut se deplacer.
	 */
	private VuePersonnage personnage;
	
	/**
	 * References RMI et vues des voisins (calcule au prealable). 
	 */
	private HashMap<Integer, Point> voisins;
	
	/**
	 * Cree un deplacement.
	 * @param personnage personnage voulant se deplacer
	 * @param voisins voisins du personnage
	 */
	public DeplacementRapide(VuePersonnage personnage, HashMap<Integer, Point> voisins) { 
		this.personnage = personnage;

		if (voisins == null) {
			this.voisins = new HashMap<Integer, Point>();
		} else {
			this.voisins = voisins;
		}
	}

	/**
	 * Deplace ce sujet d'une case en direction de l'element dont la reference
	 * est donnee.
	 * Si la reference est la reference de l'element courant, il ne bouge pas ;
	 * si la reference est egale a 0, il erre ;
	 * sinon il va vers le voisin correspondant (s'il existe dans les voisins).
	 * @param refObjectif reference de l'element cible
	 */    
	public void seDirigeVers(int refObjectif) throws RemoteException {
		Point pvers;

		// on ne bouge que si la reference n'est pas la notre
		if (refObjectif != personnage.getRefRMI()) {
			
			// la reference est nulle (en fait, nulle ou negative) : 
			// le personnage erre
			if (refObjectif <= 0) { 
				pvers = Calculs.positionAleatoireArene();
						
			} else { 
				// sinon :
				// la cible devient le point sur lequel se trouve l'element objectif
				pvers = voisins.get(refObjectif);
			}
	
			// on ne bouge que si l'element existe
			if(pvers != null) {
				seDirigeVers(pvers);
			}
		}
	}

	/**
	 * Deplace ce sujet d'une case en direction de la case donnee.
	 * @param objectif case cible
	 * @throws RemoteException
	 */
	public void seDirigeVers(Point objectif) throws RemoteException {
		Point cible = Calculs.restreintPositionArene(objectif); 											
		//On r�cup�re la vitesse de l'�l�ment.
		int vitesse = personnage.getElement().getCaract(Caracteristique.VITESSE);
		
		
		Point dest;
		
		// vitesse �gale � 1
		if (vitesse == 1)
		{	
			dest = Calculs.meilleurPoint(personnage.getPosition(), cible, voisins);
			
			if(dest != null) {
				personnage.setPosition(dest);
			}
		}	
	
		//  vitesse sup�rieure � 1
		else
		{	
			dest = Calculs.meilleurPoint(personnage.getPosition(), cible, voisins);
			Point dest1 = Calculs.meilleurPoint(dest, cible, voisins);
			
			for (int i=3; i <= vitesse; i++)
				dest1 = Calculs.meilleurPoint(dest1, cible, voisins);
			
			
			if(dest1 != null) {
				personnage.setPosition(dest1);
			}
		}	
		

			
	}

}
