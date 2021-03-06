/**
 */
package org.eclipse.incquery.examples.cps.deployment;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Application</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.incquery.examples.cps.deployment.DeploymentApplication#getBehavior <em>Behavior</em>}</li>
 *   <li>{@link org.eclipse.incquery.examples.cps.deployment.DeploymentApplication#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.incquery.examples.cps.deployment.DeploymentPackage#getDeploymentApplication()
 * @model
 * @generated
 */
public interface DeploymentApplication extends DeploymentElement {
	/**
	 * Returns the value of the '<em><b>Behavior</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Behavior</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Behavior</em>' containment reference.
	 * @see #setBehavior(DeploymentBehavior)
	 * @see org.eclipse.incquery.examples.cps.deployment.DeploymentPackage#getDeploymentApplication_Behavior()
	 * @model containment="true"
	 * @generated
	 */
	DeploymentBehavior getBehavior();

	/**
	 * Sets the value of the '{@link org.eclipse.incquery.examples.cps.deployment.DeploymentApplication#getBehavior <em>Behavior</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Behavior</em>' containment reference.
	 * @see #getBehavior()
	 * @generated
	 */
	void setBehavior(DeploymentBehavior value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.eclipse.incquery.examples.cps.deployment.DeploymentPackage#getDeploymentApplication_Id()
	 * @model
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.incquery.examples.cps.deployment.DeploymentApplication#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

} // DeploymentApplication
