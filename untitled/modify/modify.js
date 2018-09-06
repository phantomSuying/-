function add() {
    let form=document.getElementById("theForm");
    let count=form.getElementsByTagName("input").length;

    form.appendChild(document.createElement("br"));
    let text=document.createElement("p");
    text.appendChild(document.createTextNode("新信息"+String((count-2)/2)));
    form.appendChild(text);
    let first=document.createElement("input");
    first.setAttribute("id","first"+String((count-2)/2));
    first.setAttribute("name","first"+String((count-2)/2));
    let second=document.createElement("input");
    second.setAttribute("id","second"+String((count-2)/2));
    second.setAttribute("name","second"+String((count-2)/2));

    form.appendChild(first);
    form.appendChild(second);
}