/**
 * 
 */
package client;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.swing.text.Position;

import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Invocateur;
import serveur.element.Potion;
import serveur.element.Sbire;
import serveur.element.Personnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * @author clement
 *
 */
public class StrategieInvocateur extends StrategiePersonnage {
	
	/**
	 * Cree un personnage, la console associe et sa strategie.
	 * @param ipArene ip de communication avec l'arene
	 * @param port port de communication avec l'arene
	 * @param ipConsole ip de la console du personnage
	 * @param nom nom du personnage
	 * @param groupe groupe d'etudiants du personnage
	 * @param nbTours nombre de tours pour ce personnage (si negatif, illimite)
	 * @param position position initiale du personnage dans l'arene
	 * @param logger gestionnaire de log
	 */
	public StrategieInvocateur(String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			int nbTours, Point position, LoggerProjet logger) {
		
		super(ipArene, port, ipConsole, new Invocateur(nom, groupe, caracts), nbTours, position, logger);
	}
	
	/* (non-Javadoc)
	 * @see client.StrategiePersonnage#executeStrategie(java.util.HashMap)
	 */
	@Override
	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
		// TODO Auto-generated method stub
		// arene
		IArene arene = console.getArene();
		
		// reference RMI de l'element courant
		int refRMI = 0;
		
		// position de l'element courant
		Point position = null;
		
		try {
			refRMI = console.getRefRMI();
			position = arene.getPosition(refRMI);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		Element moi = arene.elementFromRef(refRMI);
//		System.out.println(((Personnage)moi).getCaract(Caracteristique.VIE)); // a enlever
//		((Personnage)moi).tue();// a enlever
//		System.out.println(((Personnage)moi).getCaract(Caracteristique.VIE)); // a enlever
		
		if (moi.getCaract(Caracteristique.MANA) >= 80)
		{
			console.setPhrase("J'invoque des sbires !");
			arene.invoquer(refRMI, Invocateur.NB_SBIRES);
//			System.out.println(((Personnage)moi).getNom()); // a enlever
//			((Personnage)moi).tue();// a enlever
//			System.out.println(((Personnage)moi).getCaract(Caracteristique.VIE)); // a enlever
		}
		else if (voisins.isEmpty()) { // je n'ai pas de voisins, j'erre
			console.setPhrase("J'erre...");
			arene.deplaceRapidement(refRMI, 0); 
			
		} else {
			int refCible = Calculs.chercheElementProche(position, voisins);
			int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

			Element elemPlusProche = arene.elementFromRef(refCible);
			
			
			
			//Caract�ristique vitesse de l'adversaire
			int invAdv = elemPlusProche.getCaract(Caracteristique.INVISIBILITE); 
			
			//L'invocateur ne peut pas acqu�rir l'invisibilit� mais quand m�me prendre les potions pour emp�cher les autres adversaires de les prendre.		
			if ((invAdv == 1) && (elemPlusProche instanceof Personnage))
			{
				console.setPhrase("Je ne peux qu'errer.");																	
				arene.deplaceRapidement(refRMI, 0);	
			}	
			
			else if (elemPlusProche instanceof Sbire && ((Sbire)elemPlusProche).getMaitre() == refRMI)
			{
				console.setPhrase("J'erre...");
				arene.deplaceRapidement(refRMI, 0);
			}
			else if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { // si suffisamment proches
				// j'interagis directement
				if(elemPlusProche instanceof Potion) { // potion
					// ramassage
					console.setPhrase("Je ramasse une potion");
					arene.ramassePotion(refRMI, refCible);

				} else { // personnage
					// duel
					console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
					arene.lanceAttaque(refRMI, refCible);
				}
				
			} else { // si voisins, mais plus eloignes
				// je vais vers le plus proche
				console.setPhrase("Je vais vers mon voisin " + elemPlusProche.getNom());
				arene.deplaceRapidement(refRMI, refCible);
			}
		}
	}
}
