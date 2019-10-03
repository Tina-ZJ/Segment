sample=$1
output=$2
cat ${sample} | python add_tab_O.py | python split_space.py | python add_tab_tag.py | python chage.py >${output}
