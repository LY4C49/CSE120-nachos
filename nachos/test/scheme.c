/*
 * Simple scheme interpreter for arithmetic expressions.
 *
 * Expressions can be arbitrarily nested lisp arithmetic expressions, e.g.,
 * the following expression:
 *
 * (+ (* 2 (/ 14 7) 3)
 *    (/ (* (- (* 3 5) 3) (+ 1 1))
 *       (- (* 4 3) (* 3 2)))
 *    (- 15 18))
 *
 * evaluates to "13".
 *
 * Geoff Voelker
 * November 2022
 */

#include "stdlib.h"
#include "stdio.h"

#ifndef NULL
#define NULL 0L
#endif

int
strtoi (char *str)
{
    int i, r = 0;

    while (str && str[0]) {
	r = r * 10 + (str[0] - '0');
	str++;
    }
    return r;
}

typedef enum cell_type {
    CELL_INT = 1,
    CELL_STR = 2,
    CELL_LIST = 3,
} cell_type;

typedef struct cell {
    cell_type type;
    union {
	int val;
	char *str;
	struct cell *car;
    } data;
    struct cell *cdr;
} cell;

int nullp (cell *c) { return (c == NULL); }
int intp (cell *c) { return (c->type == CELL_INT); }
int strp (cell *c) { return (c->type == CELL_STR); }
int listp (cell *c) { return (c->type == CELL_LIST); }
int atomp (cell *c) { return !(listp(c)); }


#define NCELLS 512

/*
 * Nachos processes do not have a heap, so use a static array
 * as a simple heap replacement.
 */
cell *
newcell ()
{
    static cell cell_storage[NCELLS];
    static int next_cell = 0; 
    
    assert (next_cell < NCELLS);
    return &cell_storage[next_cell++];
}

cell *car (cell *c) { return c->data.car; }
cell *cdr (cell *c) { return c->cdr; }
cell *setcdr (cell *c, cell *d) { return (c->cdr = d); }

void *
cons (void *a, void *b)
{
    cell *c = newcell();
    c->type = CELL_LIST;
    c->data.car = a;
    c->cdr = b;
}

cell *
new_list_cell (cell *list)
{
    cell *c = newcell ();
    c->type = CELL_LIST;
    c->data.car = list;
    c->cdr = NULL;
}

cell *
new_int_cell (int i)
{
    cell *c = newcell ();
    c->type = CELL_INT;
    c->data.val = i;
    c->cdr = NULL;
}

cell *
new_str_cell (char *str)
{
    cell *c = newcell ();
    c->type = CELL_STR;
    c->data.str = str;
    c->cdr = NULL;
}

int toint (cell *c) { return c->data.val; }
char *tostr (cell *c) { return c->data.str; }

void
printl (cell *list)
{
    while (list != NULL) {
	if (strp (list)) {
	    printf ("%s\n", tostr (list));
	} else if (intp (list)) {
	    printf ("%d\n", toint (list));
	}
	list = cdr (list);
    }
}

void
print_exp2 (cell *exp)
{
    if (intp (exp)) {
	printf (" %d", toint (exp));
    } else if (strp (exp)) {
	printf (" %s", tostr (exp));
    } else {
	while (exp) {
	    if (listp (car (exp))) printf (" (");
	    print_exp2 (car (exp));
	    if (listp (car (exp))) printf (" )");
	    exp = cdr (exp);
	}
    }
}

void
print_exp (cell *exp)
{
    print_exp2 (exp);
    printf ("\n");
}

int
isaspace (char c)
{
    return (c == '\0' || c == ' ' ||  c == '\n' || c == '\t' || c == '\r');
}

int
isadigit (char c)
{
    return ((c >= '0') && (c <= '9'));
}

/*
 * Convert a program string into a list of tokens.
 */
cell *
tokenize (char *prog)
{
    cell *tokens = NULL, *curtok = NULL;
    char nextc;
 
    nextc = *prog;
    while (nextc != '\0') {
	cell *c;
	char *t;

	if (isaspace (nextc)) {
	    prog++;
	    nextc = *prog;
	    continue;
	} else if (nextc == '(') {
	    t = "(";
	    prog++;
	    nextc = *prog;
	} else if (nextc == ')') {
	    t = ")";
	    prog++;
	    nextc = *prog;
	} else if (nextc == ';') {
	    /* comment */
	    printf ("comments not implemented\n");
	    exit (-1);
	} else {
	    t = prog;
	    while (!isaspace (*prog) && *prog != '(' && *prog != ')'
		   && *prog != '\0') {
		prog++;
	    }
	    nextc = *prog;
	    *prog = '\0';
	}

	if (isadigit (*t)) {
	    c = new_int_cell (strtoi (t));
	} else {
	    c = new_str_cell (t);
	}
	
	if (curtok == NULL) {
	    tokens = curtok = c;
	} else {
	    curtok->cdr = c;
	    curtok = c;
	}
    }
    return tokens;
}

cell *
subparse (cell *tokens, cell **cursor)
{
    cell list;
    cell *t = tokens, *elem;
    cell *curtok = &list;
    
    while (tokens) {
	if (strp (tokens) && tostr (tokens)[0] == ')') {
	    *cursor = tokens;
	    break;
	}

	if (strp (tokens) && tostr (tokens)[0] == '(') {
	    elem = new_list_cell (subparse (cdr (tokens), &tokens));
	} else {
	    elem = new_list_cell (tokens);
	}
	
	setcdr (curtok, elem);
	curtok = elem;
	tokens = cdr (tokens);
    }

    return cdr (&list);
}

/*
 * Parse a list of tokens into an expression graph.
 */
cell *
parse (cell *tokens)
{
    cell *filler;
    return car (subparse (tokens, &filler));
}

cell *
eval_primitive_op (cell *exp)
{
    int r;
    cell *atoms, *opcell, *actuals, *result;
    
    opcell = car (exp);
    actuals = cdr (exp);

    switch (tostr (opcell)[0]) {
    case '+':
	r = 0;
	while (actuals) {
	    r += toint (car (actuals));
	    actuals = cdr (actuals);
	}
	result = new_int_cell (r);
	break;

    case '-':
	if (nullp (actuals)) {
	    result = new_int_cell (0);
	} else if (nullp (cdr (actuals))) {
	    result = new_int_cell (0 - toint (car (actuals)));
	} else {
	    r = toint (car (actuals));
	    actuals = cdr (actuals);
	    while (actuals) {
		r -= toint (car (actuals));
		actuals = cdr (actuals);
	    }
	    result = new_int_cell (r);
	}
	break;
	
    case '*':
	r = 1;
	while (actuals) {
	    r *= toint (car (actuals));
	    actuals = cdr (actuals);
	}
	result = new_int_cell (r);
	break;
	
    case '/':
	r = toint (car (actuals));
	actuals = cdr (actuals);
	while (actuals) {
	    r /= toint (car (actuals));
	    actuals = cdr (actuals);
	}
	result = new_int_cell (r);
	break;
	
    default:
	printf ("unknown operator '%c'\n", tostr (opcell)[0]), exit (-1);
    }

    return result;
}

cell *scheme_map_eval (cell *);  /* forward declaration */

/*
 * Evaluate an expression graph.
 */
cell *
scheme_eval (cell *exp)
{
    if (atomp (exp)) {
	return exp;
    } else {
	cell *atoms = scheme_map_eval (exp);
	return eval_primitive_op (atoms);
    }
}

cell *
scheme_map_eval (cell *list)
{
    if (nullp (list)) return NULL;
    return cons (scheme_eval (car (list)),
		 scheme_map_eval (cdr (list)));
}

cell *
execute (char *prog)
{
    cell *expression, *tokens, *result;
    char scratch[256];

    printf ("> %s\n", prog);

    tokens = tokenize (strcpy (scratch, prog));
    expression = parse (tokens);
    result = scheme_eval (expression);

    print_exp (result);
    
    return result;
}

typedef struct test {
    char *prog;
    int expected;
} test;

void
validate_arith ()
{
    int i;
    test tests[] = {
	{ "(+)", 0 },
	{ "(+ 7)", 7 },
	{ "(+ 1 3)", 4 },
	{ "(+ 1 3 128)", 132 },
	{ "(-)", 0 },
	{ "(- 21)", -21 },
	{ "(- 21 3)", 18 },
	{ "(- 21 3 20)", -2 },
	{ "(*)", 1 },
	{ "(* 5)", 5 },
	{ "(* 5 3)", 15 },
	{ "(* 5 3 10)", 150 },
	{ "(/ 1)", 1 },
	{ "(/ 100 2)", 50 },
	{ "(/ 100 2 25)", 2 },
	{ "(+ 1 2 3 (* 4 5) 6 7)", 39 },
	{ "(+ (* 3 (+ (* 2 4) (+ 3 5)))\n\
     (+ (- 10 7) 6))", 57 },
	{ "(+ (* 2 (/ 14 7) 3)\n\
     (/ (* (- (* 3 5) 3) (+ 1 1))\n\
        (- (* 4 3) (* 3 2)))\n\
     (- 15 18))", 13 },
	{ NULL, 0 },
    };

    for (i = 0; tests[i].prog; i++) {
	assert (toint (execute (tests[i].prog)) == tests[i].expected);
    }
}

int
main (int argc, char *argv[])
{
    validate_arith();
}