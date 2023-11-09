package it.pagopa.atmlayer.wf.process.client.camunda.bean;

public interface CamundaFormRef {

  /**
   * The key of a {@link CamundaFormRef} corresponds to the {@code id} attribute
   * in the Camunda Forms JSON.
   */
  String getKey();

  /**
   * The binding of {@link CamundaFormRef} specifies which version of the form
   * to reference. Possible values are: {@code latest}, {@code deployment} and
   * {@code version} (specific version value can be retrieved with {@link #getVersion()}).
   */
  String getBinding();

  /**
   * If the {@link #getBinding() binding} of a {@link CamundaFormRef} is set to
   * {@code version}, the specific version is returned.
   */
  Integer getVersion();
}
