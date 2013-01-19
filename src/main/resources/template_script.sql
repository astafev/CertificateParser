BEGIN
sandbox.ref_cr.org_mnemonic_create(
p_region_code => '%Регион%',
p_org_name => '%Субъект%',
p_org_ogrn => '%ОГРН%');
END;

BEGIN
sandbox.ref_cr.SYSTEM_MNEMONIC_CREATE(
p_org_mnemonic => 'ХХХХ',
p_system_name => '%Название ИС%',
p_region_code => '%Регион%',
p_certificate_serial =>'%serial%',
p_tp_count => 1, p_classifier =>'M' );
END;

----------------------------------------------
