--Media
select sum(r.valor) / count(*)
from rasgo_objeto r
join objeto o on o.uid = r.id_objeto 
join clase_objeto c on o.uid = c.id_objeto
where c.id_clase = 1
and r.id_rasgo = 1; -- = 2439.796


--Desvio estandar
select sqrt(sum(power(valor,2) ) / count(*) - power(sum(valor) / count(*) ,2))
from rasgo_objeto r
join objeto o on o.uid = r.id_objeto 
join clase_objeto c on o.uid = c.id_objeto
where c.id_clase = 1
and r.id_rasgo = 1;--641.835

select sqrt(sum(power(valor,2) ) / count(*) - power(select sum(r.valor) / count(*)
													from rasgo_objeto r
													join objeto o on o.uid = r.id_objeto 
													join clase_objeto c on o.uid = c.id_objeto
													where c.id_clase = 1
													and r.id_rasgo = 1 ,2))


AREA, valor: 2279.0, devEst: 421.31417773869975
ASPECT RADIO, valor: 0.8274407980705925, devEst: 0.11452181768137916
CIRCULARIDAD, valor: 0.9245181487457813, devEst: 0.022824468971743596

select * from rasgo_objeto 
where id_rasgo = 1
and id_objeto = 7
and valor >= 2279.0 - 421.31417773869975 
and valor <= 2279.0 + 421.31417773869975 ;

select *, 0.8274407980705925 - 0.11452181768137916, 0.8274407980705925 + 0.11452181768137916 
from rasgo_objeto 
where id_rasgo = 2
and id_objeto = 7
--and valor >= 0.8274407980705925 - 0.11452181768137916
--and valor <= 0.8274407980705925 + 0.11452181768137916


select * from objeto o 
join clase_objeto c on o.uid = c.id_objeto
join rasgo_objeto r on o.uid = r.id_objeto
where id_clase  = 1 and id_rasgo  in (1,4)
order by nombre,id_rasgo
;

delete from clase_objeto;
delete from rasgo_objeto;
delete from objeto;

select * from rasgo r
join rasgo_clase rc on r.uid = rc.id_rasgo;
