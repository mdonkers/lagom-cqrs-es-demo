CREATE OR REPLACE VIEW inspections_with_remarks_vw (
    ship_name, ship_category, ucrn, dt_inspected, employee, remarks
) AS
  SELECT s.name,
    s.ship_category,
    i.ucrn,
    i.dt_inspected,
    i.employee,
    i.remarks
  FROM ship s, portvisit p, inspections i
  WHERE i.ucrn = p.ucrn
    AND p.ship_id = s.id
    AND i.remarks IS NOT NULL;
