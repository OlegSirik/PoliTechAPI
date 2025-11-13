create or replace view pt_lobs_vw as
SELECT 
    t.id,
    t.code,
    var_data ->> 'varCode' as var_code,
    var_data ->> 'varName' as var_name,
	var_data ->> 'varPath' as var_path,
	var_data ->> 'varType' as var_type,
	var_data ->> 'varValue' as var_value,
	var_data ->> 'varDataType' as var_data_type
FROM pt_lobs t,
jsonb_array_elements(t.lob -> 'mpVars') AS var_data
WHERE t.lob -> 'mpVars' IS NOT NULL;

